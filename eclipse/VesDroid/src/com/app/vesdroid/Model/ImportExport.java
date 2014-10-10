package com.app.vesdroid.Model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

public class ImportExport {
	private static final String PROTOCOL_BEGIN = "PROTOCOL_BEGIN";
	private static final String PROTOCOL_END = "PROTOCOL_END";
	
	private static File validateExport(Context context, String fileName){
	    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
	    	Toast.makeText(context, "Ёкспорт не возможен: недоступна карта пам€ти", Toast.LENGTH_LONG).show();
	      return null;
	    }

	    File filePath = Environment.getExternalStorageDirectory();
	    File file = new File(filePath, fileName);
	    if (file.exists()){
	    	Toast.makeText(context
	    			, "Ёкспорт не возможен: файл с названием \n\"" + fileName + "\"\n уже существует"
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
	        intent.setType("text/csv");
	        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file) );  
	        context.startActivity(Intent.createChooser(intent, "ќтправить " + file.getName()));
	}
	
	public static void importData(Context context, Uri uri){
		try{
			BufferedReader reader = new BufferedReader(new FileReader(uri.getPath()));
			String line;
			String file = "";
			while ((line = reader.readLine()) != null) {
				if(line.contains(PROTOCOL_BEGIN)){
					line = reader.readLine();
					if (line == null) continue;
					
					Protocol protocol = new Protocol();
					line = line.trim();
					if (line.equals("")) line = "_";
					protocol.setName(line);
					ArrayList<ABMN> abmns = protocol.getABMNs();
					
					while (true) {
						line = reader.readLine();
						if (line == null || line.contains(PROTOCOL_END)) break;
						
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
			
			Toast.makeText(context, "»мпорт успешно завершен", Toast.LENGTH_SHORT).show();
		}
		catch (Exception e){
			Toast.makeText(context, "ќшибка импорта:\n" + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	public static void exportData(Context context, Protocol protocol){
	    String fileName = protocol.getName() + ".csv";
	    File file = validateExport(context, fileName);
	    
	    if (file == null) return;
	    
	    String fileContent = PROTOCOL_BEGIN + "\n"
	    		+ protocol.getName() + "\n";
	    ArrayList<ABMN> abmns = protocol.getABMNs();
	    for (ABMN abmn : abmns) {
			fileContent += abmn.getA() + ";"
					+ abmn.getB() + ";" +
					+ abmn.getM() + ";" +
					+ abmn.getN() + "\n";
		}
	    fileContent += PROTOCOL_END;
	    
	    writeAndSend(context, file, fileContent);
	}
}
