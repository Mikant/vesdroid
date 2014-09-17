using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;

namespace Vespa.Data {
    class Program {
        static void Main(string[] args) {
            var protocol = new Protocol(ProtocolUtil.CreateDummySchlumbergerArray(1, 10, 1.1f)) { Name = @"MyProtocol" };
            
            var project = new Project {
                Operator = @"Vasily",
                Protocol = protocol
            };

            var profile = project.NewProfile(@"MyProfile");
            var site = profile.NewSite(@"MySite");

            site.CreateRecordsList();

            var first = site.Records.First;

            first.Value.U = 10;
            
            ((SchlumbergerProxy)site.Records.First.Value.Quadrupole).MN = 10;

            site.Records.AddAfter(first, new DataRecord(site) { U = 100 });
        }
    }

    public interface IQuadrupole {
        float A { get; }
        float B { get; }
        float M { get; }
        float N { get; }
    }

    public struct Quadrupole : IQuadrupole { // запись протокола
        private readonly float _a;
        private readonly float _b;
        private readonly float _m;
        private readonly float _n;

        public static readonly Quadrupole Empty = default(Quadrupole);

        public Quadrupole(float a, float b, float m, float n) {
            _a = a;
            _b = b;
            _m = m;
            _n = n;
        }

        public float A { get { return _a; } }
        public float B { get { return _b; } }
        public float M { get { return _m; } }
        public float N { get { return _n; } }

        public static bool Equals(Quadrupole q1, Quadrupole q2) {
            const float EPSILON = 1e-3f;

            return
                Math.Abs(q1._a - q2._a) < EPSILON &&
                Math.Abs(q1._b - q2._b) < EPSILON &&
                Math.Abs(q1._m - q2._m) < EPSILON &&
                Math.Abs(q1._n - q2._n) < EPSILON;
        }

        public override bool Equals(object obj) {
            return obj is Quadrupole && Equals(this, (Quadrupole)obj);
        }

        public static bool operator ==(Quadrupole q1, Quadrupole q2) {
            return Equals(q1, q2);
        }
        public static bool operator !=(Quadrupole q1, Quadrupole q2) {
            return !(q1 == q2);
        }

        public override int GetHashCode() {
            return _a.GetHashCode() ^ _b.GetHashCode() ^ _m.GetHashCode() ^ _n.GetHashCode();
        }
    }

    public class Protocol { // протокол
        private readonly List<Quadrupole> _quadrupoles;

        public Protocol(IEnumerable<Quadrupole> quadrupoles) {
            _quadrupoles = new List<Quadrupole>(quadrupoles);
        }

        public ReadOnlyCollection<Quadrupole> Quadrupoles { get { return _quadrupoles.AsReadOnly(); } }

        public string Name { get; set; }
    }

    public interface ICommonMetadata {
        string Operator { get; set; }
        Protocol Protocol { get; set; }
    }

    public class Project : ICommonMetadata { // проект
        private readonly List<Profile> _profiles = new List<Profile>();

        public ReadOnlyCollection<Profile> Profiles { get { return _profiles.AsReadOnly(); } }

        public string Name { get; set; }
        public string Comment { get; set; }

        // ICommonMetadata
        public string Operator { get; set; }
        public Protocol Protocol { get; set; }

        public Profile NewProfile(string name) {
            var p = new Profile(this) { Name = name };
            _profiles.Add(p);
            return p;
        }
    }

    public class Profile : ICommonMetadata { // профиль
        private readonly Project _project;

        private readonly LinkedList<DataSite> _sites = new LinkedList<DataSite>();

        private string _operator;
        private Protocol _protocol;

        public Profile(Project project) {
            _project = project;
        }

        public LinkedList<DataSite> Sites {
            get { return _sites; }
        }

        public string Name { get; set; }
        public string Comment { get; set; }

        // ICommonMetadata
        public string Operator {
            get { return _project.Operator ?? _operator; }
            set { _operator = value; }
        }
        public Protocol Protocol {
            get { return _project.Protocol ?? _protocol; }
            set { _protocol = value; }
        }

        public DataSite NewSite(string name) {
            var s = new DataSite(this) { Name = name };
            _sites.AddLast(s);
            return s;
        }
    }

    public class DataSite : ICommonMetadata { // пикет
        private readonly Profile _profile;

        private readonly LinkedList<DataRecord> _records = new LinkedList<DataRecord>();

        private string _operator;
        private Protocol _protocol;

        public DataSite(Profile profile) {
            _profile = profile;
        }

        public string Comment { get; set; }
        public string Name { get; set; }

        // ICommonMetadata
        public string Operator {
            get { return _profile.Operator ?? _operator; }
            set { _operator = value; }
        }
        public Protocol Protocol {
            get { return _profile.Protocol ?? _protocol; }
            set { _protocol = value; }
        }

        public LinkedList<DataRecord> Records {
            get { return _records; }
        }

        public void CreateRecordsList() {
            var p = Protocol;
            if (p == null)
                throw new InvalidOperationException();

            foreach (var quadrupole in p.Quadrupoles)
                Records.AddLast(new DataRecord(this) { Quadrupole = new SchlumbergerProxy(quadrupole) });
        }
    }

    public class DataRecord : ICommonMetadata { // запись данных
        private readonly DataSite _dataSite;

        private string _operator;
        private Protocol _protocol;

        public DataRecord(DataSite dataSite) {
            _dataSite = dataSite;
        }

        public float U { get; set; }
        public float I { get; set; }

        public string Comment { get; set; }
        public QuadrupoleProxy Quadrupole { get; set; }
        public DateTime Timestamp { get; set; }

        // ICommonMetadata
        public string Operator {
            get { return _dataSite.Operator ?? _operator; }
            set { _operator = value; }
        }
        public Protocol Protocol {
            get { return _dataSite.Protocol ?? _protocol; }
            set { _protocol = value; }
        }
    }

    public static class QuadropoleEx {
        public static Quadrupole WithA(this IQuadrupole q, float value) {
            return new Quadrupole(value, q.B, q.M, q.N);
        }
        public static Quadrupole WithB(this IQuadrupole q, float value) {
            return new Quadrupole(q.A, value, q.M, q.N);
        }
        public static Quadrupole WithM(this IQuadrupole q, float value) {
            return new Quadrupole(q.A, q.B, value, q.N);
        }
        public static Quadrupole WithN(this IQuadrupole q, float value) {
            return new Quadrupole(q.A, q.B, q.M, value);
        }
        public static double GetCoefficient(this IQuadrupole q) {
            return 2 * Math.PI / (1 / Math.Abs(q.A - q.M) - 1 / Math.Abs(q.A - q.N) - 1 / Math.Abs(q.B - q.M) + 1 / Math.Abs(q.B - q.N));
        }
    }

    public abstract class QuadrupoleProxy : IQuadrupole {
        private Quadrupole _quadrupole;

        public QuadrupoleProxy(Quadrupole quadrupole) {
            _quadrupole = quadrupole;
        }

        public float A {
            get { return _quadrupole.A; }
            protected set { _quadrupole = _quadrupole.WithA(value); }
        }
        public float B {
            get { return _quadrupole.A; }
            protected set { _quadrupole = _quadrupole.WithB(value); }
        }
        public float M {
            get { return _quadrupole.A; }
            protected set { _quadrupole = _quadrupole.WithM(value); }
        }
        public float N {
            get { return _quadrupole.A; }
            protected set { _quadrupole = _quadrupole.WithN(value); }
        }

        public Quadrupole ToQuadrupole() {
            return _quadrupole;
        }
    }

    public class SchlumbergerProxy : QuadrupoleProxy {
        public SchlumbergerProxy(Quadrupole quadrupole)
            : base(Validate(quadrupole)) {
        }

        private static Quadrupole Validate(Quadrupole q) {
            if (q.A != -q.B || q.M != -q.N)
                throw new ArgumentException();

            return q;
        }

        public float ABdiv2 {
            get { return Math.Abs(A - B) / 2; }
            set {
                A = -value;
                B = value;
            }
        }

        public float MN {
            get { return Math.Abs(M - N); }
            set {
                M = -value / 2;
                N = value / 2;
            }
        }
    }

    public static class ProtocolUtil {
        public static List<Quadrupole> CreateDummySchlumbergerArray(float minABdiv2, int count, float q) {
            if (minABdiv2 <= 0 || count <= 0 || q <= 1)
                throw new ArgumentException();

            var quads = new List<Quadrupole>();
            for (int i = 0; i < count; i++, minABdiv2 *= q)
                quads.Add(new SchlumbergerProxy(Quadrupole.Empty) { ABdiv2 = minABdiv2, MN = 0.5f }.ToQuadrupole());

            return quads;
        }
    }

}
