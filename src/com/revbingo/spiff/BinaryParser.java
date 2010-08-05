package com.revbingo.spiff;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

import com.revbingo.spiff.events.EventDispatcher;
import com.revbingo.spiff.instructions.Instruction;
import com.revbingo.spiff.parser.ParseException;
import com.revbingo.spiff.parser.SpiffParser;

public class BinaryParser {

	private List<Instruction> instructions;
	private EventDispatcher eventDispatcher;
		
	public BinaryParser(EventDispatcher ed){	
		this.eventDispatcher = ed;
	}
	
	public void parse(File adfFile, File parseFile) throws BinaryParseException, ExecutionException {
		parseAdf(adfFile);
		read(parseFile);
	}

	private void parseAdf(File adfFile) throws BinaryParseException { 
		try {
            FileInputStream fis = new FileInputStream(adfFile);
            if(fis != null){
                    SpiffParser parser = new SpiffParser(fis);
                    parser.start();
                    instructions = parser.getInstructions();
            }else{
                    throw new BinaryParseException("InputStream is null");
            }
            
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			throw new BinaryParseException("Error in adf file", e);
		} 
	}

	private void read(File f) throws ExecutionException {
		try {
			FileChannel fc = new FileInputStream(f).getChannel();
			ByteBuffer buffer = ByteBuffer.allocate((int) f.length());			
			fc.read(buffer);
			buffer.flip();
						
			for(Instruction ins : instructions){
				ins.execute(buffer, eventDispatcher);
			}
		} catch (FileNotFoundException e) {
			throw new ExecutionException("Could not find file to read", e);
		} catch (IOException e) {
			throw new ExecutionException("Could not read file " + f.getAbsolutePath(), e);
		}
	
	}

}
