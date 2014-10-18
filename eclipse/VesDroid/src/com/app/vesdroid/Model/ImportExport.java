package com.app.vesdroid.Model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.UUID;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;
import android.widget.Toast;

public class ImportExport {
	private static final String PROTOCOL_BEGIN = "PROTOCOL_BEGIN";
	private static final String PROTOCOL_END = "PROTOCOL_END";
	
	private static final String PROJECT = "project";
	private static final String PROFILE = "profile";
	private static final String PICKET = "picket";
	private static final String RECORD = "record";
	private static final String NAME = "name";
	private static final String COMMENT = "comment";
	private static final String A = "A";
	private static final String B = "B";
	private static final String M = "M";
	private static final String N = "N";
	private static final String DELTA_U = "deltaU";
	private static final String I = "I";
	private static final String DATE_TIME = "dateTime";
	
	private static File validateExport(Context context, String fileName){
	    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
	    	Toast.makeText(context, "Ёкспорт невозможен: недоступна карта пам€ти", Toast.LENGTH_LONG).show();
	      return null;
	    }

	    File filePath = Environment.getExternalStorageDirectory();
	    File file = new File(filePath, fileName);
	    if (file.exists()){
	    	Toast.makeText(context
	    			, "Ёкспорт невозможен: файл с именем \n\"" + fileName + "\"\n уже существует"
	    			, Toast.LENGTH_LONG).show();
	    	return null;
	    }
	    
	    return file;
	}
	
	private static void writeAndSend(Context context, File file, String fileContent){
	    try {
		      BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		      writer.write(fileContent);
		      writer.close();
		      Toast.makeText(context, "Ёкспорт успешно завершен", Toast.LENGTH_SHORT).show();
		    } catch (Exception e) {
		    	Toast.makeText(context, "ќшибка экспорта:\n" + e.getMessage(), Toast.LENGTH_LONG).show();
		    	return;
		    }
		    
	        Intent intent = new Intent();  
	        intent.setAction(Intent.ACTION_SEND);  
	        intent.setType("text/plain");
	        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file) );  
	        context.startActivity(Intent.createChooser(intent, "ќтправить " + file.getName()));
	}
	
	public static void importData(Context context, Uri uri){
		try{
     			BufferedReader reader = new BufferedReader(new FileReader(uri.getPath()));
			
			// пробуем прочитать XML
			try {
				XmlPullParser xmlPullParser = Xml.newPullParser();
				xmlPullParser.setInput(reader);
				UUID projectId = null;
				while (xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT) {
					if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
						if (xmlPullParser.getName().equals(PROJECT)) {
							Project project = new Project();
							project.setName(xmlPullParser.getAttributeValue(null, NAME));
							project.setComment(xmlPullParser.getAttributeValue(null, COMMENT));
							ProjectManager.saveOrUpdateProject(context, project);
							projectId = project.getId();
						}
						else if(xmlPullParser.getName().equals(PROFILE)) {
							if(projectId == null) {
								Project project = new Project();
								project.setName("јвтоматический проект");
								project.setComment("ѕроект создан автоматически при импорте профил€");
								ProjectManager.saveOrUpdateProject(context, project);
								projectId = project.getId();
							}
							
							parseProfile(context, xmlPullParser, projectId);
						}
					}
					
					xmlPullParser.next();
				}
			}
			// не получилось, может быть это CSV 
			catch (Exception e) {
				reader.close();
				reader = new BufferedReader(new FileReader(uri.getPath()));
				
				String line;
				String file = "";
				while ((line = reader.readLine()) != null) {
					if (line == null) continue;
					
					Protocol protocol = new Protocol();
					line = line.trim();
					if (line.equals("")) line = "_";
					protocol.setName(line);
					ArrayList<ABMN> abmns = protocol.getABMNs();
					
					while (true) {
						line = reader.readLine();
						if (line == null) break;
						
						String[] sa = line.split(";");
						ABMN abmn = new ABMN();
						abmn.setA(Float.parseFloat(sa[0]));
						abmn.setB(Float.parseFloat(sa[1]));
						abmn.setM(Float.parseFloat(sa[2]));
						abmn.setN(Float.parseFloat(sa[3]));
						abmns.add(abmn);	
					}
					
					if (abmns.size() > 0)
						ProtocolManager.saveOrUpdateProtocol(context, protocol, true);
				}
			}
			
			reader.close();
			Toast.makeText(context, "»мпорт успешно завершен", Toast.LENGTH_SHORT).show();
		}
		catch (Exception e){
			Toast.makeText(context, "ќшибка импорта:\n" + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	private static void parseProfile(Context context, XmlPullParser xmlPullParser, UUID projectId) 
			throws XmlPullParserException, IOException {
		
		UUID profileId = null;
		UUID picketId = null;
		while (xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT
				&& !(xmlPullParser.getEventType() == XmlPullParser.END_TAG 
						&& xmlPullParser.getName().equals(PROFILE))) {
			if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
				if(xmlPullParser.getName().equals(PROFILE)) {
					Profile profile = new Profile();
					profile.setProjectId(projectId);
					profile.setName(xmlPullParser.getAttributeValue(null, NAME));
					profile.setComment(xmlPullParser.getAttributeValue(null, COMMENT));
					ProfileManager.saveOrUpdateProfile(context, profile);
					profileId = profile.getId();
				}
				else if (xmlPullParser.getName().equals(PICKET)) {
					Picket picket = new Picket();
					picket.setProfileId(profileId);
					picket.setName(xmlPullParser.getAttributeValue(null, NAME));
					picket.setComment(xmlPullParser.getAttributeValue(null, COMMENT));
					PicketManager.saveOrUpdatePicket(context, picket);
					picketId = picket.getId();
				}
				else if (xmlPullParser.getName().equals(RECORD)) {
					Record record = new Record();
					record.setPicketId(picketId);
					record.setA(Float.parseFloat(xmlPullParser.getAttributeValue(null, A)));
					record.setB(Float.parseFloat(xmlPullParser.getAttributeValue(null, B)));
					record.setM(Float.parseFloat(xmlPullParser.getAttributeValue(null, M)));
					record.setN(Float.parseFloat(xmlPullParser.getAttributeValue(null, N)));
					record.setDeltaU(Float.parseFloat(xmlPullParser.getAttributeValue(null, DELTA_U)));
					record.setI(Float.parseFloat(xmlPullParser.getAttributeValue(null, I)));
					record.setDateTimeMillis(Long.parseLong(xmlPullParser.getAttributeValue(null, DATE_TIME)));
					RecordManager.saveRecord(context, record, picketId);
				}
			}
			
			xmlPullParser.next();
		}
	}
	
	public static void exportProtocol(Context context, Protocol protocol){
	    String fileName = protocol.getName() + ".vesprot";
	    File file = validateExport(context, fileName);
	    
	    if (file == null) return;
	    
	    String fileContent = protocol.getName() + "\n";
	    ArrayList<ABMN> abmns = protocol.getABMNs();
	    for (ABMN abmn : abmns) {
			fileContent += abmn.getA() + ";"
					+ abmn.getB() + ";" +
					+ abmn.getM() + ";" +
					+ abmn.getN() + "\n";
		}
	    
	    writeAndSend(context, file, fileContent);
	}
	
	public static void exportProfile(Context context, Profile profile){
	    String fileName = profile.getName() + ".vesprof";
	    File file = validateExport(context, fileName);
	    
	    if (file == null) return;
	    
	    String fileContent = "";
	    try {
	    	BufferedWriter writer = new BufferedWriter(new FileWriter(file));
	    	XmlSerializer xmlSerializer = Xml.newSerializer();
			xmlSerializer.setOutput(writer);
			xmlSerializer.startDocument("UTF-8", null);
			
			serializeProfile(context, xmlSerializer, profile);
		    
		    xmlSerializer.endDocument();
		    xmlSerializer.flush();
		    writer.close();

		} catch (Exception e) {
		}

        Intent intent = new Intent();  
        intent.setAction(Intent.ACTION_SEND);  
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file) );  
        context.startActivity(Intent.createChooser(intent, "ќтправить " + file.getName()));
	}
	
	public static void exportProject(Context context, Project project){
	    String fileName = project.getName() + ".vesproj";
	    File file = validateExport(context, fileName);
	    
	    if (file == null) return;
	    
	    String fileContent = "";
	    try {
	    	BufferedWriter writer = new BufferedWriter(new FileWriter(file));
	    	XmlSerializer xmlSerializer = Xml.newSerializer();
			xmlSerializer.setOutput(writer);
			xmlSerializer.startDocument("UTF-8", null);
			
			xmlSerializer.startTag(null, PROJECT);
			xmlSerializer.attribute(null, NAME, project.getName());
			xmlSerializer.attribute(null, COMMENT, project.getComment());
			
			ArrayList<Profile> profiles = ProfileManager.getAllProfilesForProject(context, project.getId());
			for (int i = 0; i < profiles.size(); i++) {
				serializeProfile(context, xmlSerializer, profiles.get(i));
	    	}
			
			xmlSerializer.endTag(null, PROJECT);
		    
		    xmlSerializer.endDocument();
		    xmlSerializer.flush();
		    writer.close();

		} catch (Exception e) {
		}

        Intent intent = new Intent();  
        intent.setAction(Intent.ACTION_SEND);  
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file) );  
        context.startActivity(Intent.createChooser(intent, "ќтправить " + file.getName()));
	}
	
	private static void serializeProfile(Context context, XmlSerializer xmlSerializer, Profile profile) 
			throws IllegalArgumentException, IllegalStateException, IOException{
		xmlSerializer.startTag(null, PROFILE);
		xmlSerializer.attribute(null, NAME, profile.getName());
		xmlSerializer.attribute(null, COMMENT, profile.getComment());
    
	    ArrayList<Picket> pickets = PicketManager.getAllPicketsForProfile(context, profile.getId());
	    for (int i = 0; i < pickets.size(); i++) {
	    	Picket picket = pickets.get(i);
			xmlSerializer.startTag(null, PICKET);
			xmlSerializer.attribute(null, NAME, picket.getName());
			xmlSerializer.attribute(null, COMMENT, picket.getComment());

			ArrayList<Record> records = RecordManager.getAllRecordsForPicket(context, picket.getId());
			for (int j = 0; j < records.size(); j++) {
				Record record = records.get(j);
				xmlSerializer.startTag(null, RECORD);
				xmlSerializer.attribute(null, A, "" + record.getA());
				xmlSerializer.attribute(null, B, "" + record.getB());
				xmlSerializer.attribute(null, M, "" + record.getM());
				xmlSerializer.attribute(null, N, "" + record.getN());
				xmlSerializer.attribute(null, DELTA_U, "" + record.getDeltaU());
				xmlSerializer.attribute(null, I, "" + record.getI());
				xmlSerializer.attribute(null, DATE_TIME, "" + record.getDateTimeMillis());
				xmlSerializer.endTag(null, RECORD);
			}
			
			xmlSerializer.endTag(null, PICKET);
		}
	    
	    xmlSerializer.endTag(null, PROFILE);
	}
}
