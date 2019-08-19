package com.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

//Need to remove unwanted print statements
public class SortFiles {
	private static Logger Logger = LoggerFactory.getLogger(SortFiles.class);

	public static void main(String[] args) {
		try {
			Logger.info("In main() of Sort");
			if (args.length < 4) {
				Logger.error("Error: Sort incorrect arguments");
				System.exit(-1);
			}
			String programName = args[0];
			String inputFilePath = args[1];
			String outputFilePath = args[2];
			String sortCardName = args[3];
			boolean newLineRequired = false;
			newLineRequired = (args.length > 4 && args[4] != null && (args[4].equals("yes") || args[4].equals("true")))
					? true
					: false;

			Logger.debug("newLineRequired::" + newLineRequired);

			ArrayList<KeyMetaData> keyMetaData = new ArrayList<KeyMetaData>();
			ArrayList<SplitMetaData> splitMetaData = new ArrayList<SplitMetaData>();

			EBCDIC = false;
			JsonObject conditionDetails = null;
			try {
				Logger.debug("Get resource starts");
				URL url = Thread.currentThread().getContextClassLoader().getResource(sortCardName);
				Logger.info(
						"Got url::" + url + " url content:: " + url.getContent() + " file content:: " + url.getFile());
				BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
				String inputLine;
				String fileContent = "";
				while ((inputLine = in.readLine()) != null) {
					fileContent += inputLine;
				}
				in.close();
				conditionDetails = (JsonObject) new JsonParser().parse(fileContent);
			} catch (Exception e) {
				Logger.error("Error in reading sort card");
				e.printStackTrace();
				System.exit(-1);
			}

			if (!conditionDetails.has(programName)) {
				JsonObject programs = conditionDetails.get("program-map").getAsJsonObject();
				if (null != programs) {
					if (null != programs.get(programName)) {
						programName = programs.get(programName).getAsString();
					} else {
						Logger.error("Error: Program sort card does not exist");
						System.exit(0);
					}
				}
			}
			JsonObject programDtl = conditionDetails.get(programName).getAsJsonObject();

			JsonArray inputFiles = programDtl.get("input_files").getAsJsonArray();

			JsonArray outputFiles = programDtl.get("output_files").getAsJsonArray();
			ArrayList<String> filesToWrite = new ArrayList<String>();

			for (int cnt = 0; cnt < outputFiles.size(); cnt++) {
				filesToWrite.add(outputFiles.get(cnt).getAsString());
			}

			if (null != programDtl.get("sort_keys")) {
				JsonArray keyConditions = programDtl.get("sort_keys").getAsJsonArray();
				for (int k = 0; k < keyConditions.size(); k++) {
					int StartIndex = keyConditions.get(k).getAsJsonObject().get("key_start").getAsInt();
					int length = keyConditions.get(k).getAsJsonObject().get("key_length").getAsInt();
					String sortType = keyConditions.get(k).getAsJsonObject().get("sort_type").getAsString();
					keyMetaData.add(new KeyMetaData(StartIndex - 1, ((length + StartIndex) - 1), sortType));
				}
			}
			if (null != programDtl.get("split")) {
				JsonArray splitConditions = programDtl.get("split").getAsJsonArray();
				for (int s = 0; s < splitConditions.size(); s++) {
					int splitStart = splitConditions.get(s).getAsJsonObject().get("split_start").getAsInt();
					int splitLength = splitConditions.get(s).getAsJsonObject().get("split_length").getAsInt();
					String splitValue = splitConditions.get(s).getAsJsonObject().get("split_value").getAsString();
					String splitFileName = splitConditions.get(s).getAsJsonObject().get("split_file").getAsString();
					int splitRecordLength = 0;
					if(null!=splitConditions.get(s).getAsJsonObject().get("split_record_length")) {
						splitRecordLength = splitConditions.get(s).getAsJsonObject().get("split_record_length").getAsInt();
					}
					splitMetaData.add(new SplitMetaData(splitStart - 1, (splitLength - (splitStart - 1)), splitValue,
							outputFilePath + splitFileName,splitRecordLength, newLineRequired));
				}
			}

			ArrayList<FileHandler> randomAccessFiles = new ArrayList<FileHandler>();
			Logger.info("Reading input files...");
			long readStart = System.currentTimeMillis();
			ArrayList<Thread> readerThreads = new ArrayList<Thread>();
			
			String singleOutput=(!filesToWrite.isEmpty()&& filesToWrite.size()==1)?filesToWrite.get(0):null;
			String fileToWrite = singleOutput;
			for (int ipcnt = 0; ipcnt < inputFiles.size(); ipcnt++) {
				if(null==singleOutput && inputFiles.size() == filesToWrite.size()) {
					fileToWrite = filesToWrite.get(ipcnt);
				}else {
					//LOGGER.warn("This case is not handled. Writing to first output file. Please review sort card again!!!");
					fileToWrite=singleOutput;
				}
				String inputFileName = inputFiles.get(ipcnt).getAsJsonObject().get("input_file_name").getAsString();
				recordLength = inputFiles.get(ipcnt).getAsJsonObject().get("record_length").getAsInt();
				EBCDIC = (inputFiles.get(ipcnt).getAsJsonObject().get("ebcdic").getAsString().equals("yes")
						|| inputFiles.get(ipcnt).getAsJsonObject().get("ebcdic").getAsString().equals("true")) ? true
								: false;
				Logger.info("Reading from file:: " + inputFilePath + inputFileName);
				FileHandler fileHandler = new FileHandler(
						new RandomAccessFile(new File(inputFilePath + inputFileName), "rw"));
				Logger.debug("got fileHandler:: " + fileHandler);
				randomAccessFiles.add(fileHandler);
				Thread readerThread = new Thread(new FileReader(inputFilePath + inputFileName, fileHandler, keyMetaData,
						recordLength, splitMetaData,fileToWrite, EBCDIC));

				readerThread.start();
				readerThreads.add(readerThread);
			}
			for (Thread t : readerThreads) {
				try {
					t.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.exit(-1);
				}
			}
			readerThreads.clear();
			Logger.info("NumberRecords= " + numberRecords);
			Logger.info("Done reading (" + (System.currentTimeMillis() - readStart) + ").");
			Logger.info("Sorting logic started...");
			long sortStart = System.currentTimeMillis();
			RecordKey tmpRecordData[] = new RecordKey[keys.size()];
			keys.toArray(tmpRecordData);

			ArrayList<Thread> splitThreads = new ArrayList<Thread>();
			
			if(!splitMetaData.isEmpty()) {
				Logger.info("Splitting logic started");
				for (SplitMetaData smd : splitMetaData) {
					Thread writerThread = new Thread(smd);
					writerThread.start();
					splitThreads.add(writerThread);
				}
				for (Thread t : splitThreads) {
					try {
						t.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
						System.exit(-1);
					}
				}
				Logger.info("\n Done with splitting");
			}

			if (!keyMetaData.isEmpty()) {
				Logger.info("Comparing logic started...");
				Arrays.parallelSort(tmpRecordData);
				Logger.info("Compared data in time:: (" + (System.currentTimeMillis() - sortStart) + ").");
			}
			Logger.info("Writing output...");
			long writeStart = System.currentTimeMillis();
			ArrayList<Thread> writerThreads = new ArrayList<Thread>();
			for (String writeFile : filesToWrite) {
				FileWriter outputFile = new FileWriter(outputFilePath + writeFile, newLineRequired);
				outputFile.inputRecord = tmpRecordData;
				Thread writerThread = new Thread(outputFile);
				writerThread.start();
				writerThreads.add(writerThread);
			}
			for (Thread t : writerThreads) {
				try {
					t.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.exit(-1);
				}
			}

			Logger.info("Wrote in time::  (" + (System.currentTimeMillis() - writeStart) + ").");

			for (FileHandler fileHandler : randomAccessFiles) {
				fileHandler.file.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		Logger.info("Sorting Done...");
		System.exit(0);
	}

	static class FileWriter implements Runnable {
		public FileWriter(String outputFileName, boolean newLineRequired) throws FileNotFoundException {
			this.outputFileObj = new File(outputFileName);
			if (outputFileObj.exists()) {
				outputFileObj.delete();
			}
			this.newLineRequired = newLineRequired;
			Logger.info("outputFileName:: " + outputFileName);
		}

		@Override
		public void run() {
			long totalWriteTime = 0;
			long totalReadTime = 0;
			ByteBuffer buffer = ByteBuffer.allocate(1024 * 8);
			FileChannel owc = null;
			try {
				FileOutputStream outputStream = new FileOutputStream(outputFileObj, !this.newLineRequired);
				owc = outputStream.getChannel();
				for (int i = 0; i < inputRecord.length; i++) {
					String writeToFileFromRecord = inputRecord[i].writeToFile;
					String currentOutputFileName = outputFileObj.getName();
					if (inputRecord[i].isSplitted || !(currentOutputFileName.equals(writeToFileFromRecord))) {
						continue;
					}
					try {
						long b = System.currentTimeMillis();
						totalReadTime += (System.currentTimeMillis() - b);
						b = System.currentTimeMillis();

						buffer.put(inputRecord[i].recordData);
						if (this.newLineRequired) {
							buffer.put("\n".getBytes());
						}
						buffer.flip();

						inputRecord[i].isProcessed = true;
						owc.write(buffer);
						buffer.clear();
						totalWriteTime += (System.currentTimeMillis() - b);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				owc.close();
				outputStream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			Logger.info("Writing Time:: totalReadTime=" + totalReadTime + " totalWriteTime=" + totalWriteTime);

		}

		RecordKey inputRecord[];
		String outputFileName;
		File outputFileObj;
		boolean newLineRequired;
	}

	static class FileReader implements Runnable {
		public FileReader(String fileToRead, FileHandler fileHandler, ArrayList<KeyMetaData> keyMetaData,
				int recordLength, ArrayList<SplitMetaData> splitMetaData, String writeToFile, boolean isEBCDIC) {
			this.fileToRead = fileToRead;
			this.fileHandler = fileHandler;
			this.keyMetaData = keyMetaData;
			this.recordLength = recordLength;
			this.splitMetaData = splitMetaData;
			this.writeToFile=writeToFile;
		}

		@Override
		public void run() {
			byte record[] = new byte[this.recordLength];
			try {
				ByteBuffer recordBuffer = ByteBuffer.allocate(1024 * 8);
				int numberRecordsInFile = 0;
				ArrayList<RecordKey> recordKeys = new ArrayList<RecordKey>();
				while (fileHandler.channel.read(recordBuffer) != -1) {
					recordBuffer.flip();
					while (recordBuffer.hasRemaining() && recordBuffer.remaining() >= this.recordLength) {
						record = new byte[this.recordLength];
						recordBuffer.get(record);
						RecordKey recordKey = new RecordKey();
						recordKey.recordNumber = numberRecordsInFile;
						recordKey.fileHandler = fileHandler;
						recordKey.recordData = new byte[record.length];
						recordKey.keyMetaList = keyMetaData;
						recordKey.writeToFile = this.writeToFile;
						int keyPosition = 0;
						System.arraycopy(record, 0, recordKey.recordData, keyPosition, record.length);
						int tmpRecordLength = this.recordLength;
						boolean match = false;
						if (!splitMetaData.isEmpty()) {
							int splitRecordLength = this.recordLength;
							for (SplitMetaData splitData : splitMetaData) {
								if(splitData.splitRecordLenght>0) {
									splitRecordLength = splitData.splitRecordLenght;
								}
								ArrayList<byte[]> splitRecords = new ArrayList<byte[]>();
								match = true;
								for (int v = 0; v < splitData.valueBytes.length; v++) {
									match = true;
									for (int j = 0; j < splitData.splitLength; j++) {
										if (record[j + splitData.start] != splitData.valueBytes[v][j]) {
											match = false;
											break;
										}
									}
									if (match) {
										break;
									}
								}
								if (match) {
									recordKey.isSplitted = true;
									if(splitRecordLength!=this.recordLength) {
										recordBuffer.position(recordBuffer.position() - this.recordLength);
										if(splitRecordLength < this.recordLength) {
											tmpRecordLength = splitRecordLength;
										}else if(splitRecordLength > this.recordLength) {
											tmpRecordLength = this.recordLength + splitRecordLength;
										}
										
										record = new byte[tmpRecordLength];
										recordKey.recordData = new byte[tmpRecordLength];
										recordBuffer.get(record);
									}
									byte tmpRecord[] = new byte[record.length];
									System.arraycopy(record, 0, tmpRecord, 0, record.length);
									splitRecords = splitData.getSplitRecords();
									splitRecords.add(tmpRecord);
									splitData.setSplitRecords(splitRecords);
								}
							}
						}
						numberRecordsInFile += 1;
						if (recordBuffer.position() < recordBuffer.limit()) {
							if ((char) recordBuffer.get(recordBuffer.position()) == '\n'
									|| (char) recordBuffer.get(recordBuffer.position()) == '\r') {
								recordBuffer.position(recordBuffer.position() + 1);
							}
						}
						if (recordBuffer.position() < recordBuffer.limit()) {
							if ((char) recordBuffer.get(recordBuffer.position()) == '\n'
									|| (char) recordBuffer.get(recordBuffer.position()) == '\r') {
								recordBuffer.position(recordBuffer.position() + 1);
							}
						}
						if(!match) {
							recordKeys.add(recordKey);
						}
						if(recordBuffer.remaining()  < this.recordLength && recordBuffer.position() + this.recordLength <= recordBuffer.capacity()) {
							this.recordLength = recordBuffer.remaining();
						}
						/*
						 * if(recordBuffer.remaining() < this.recordLength && (recordBuffer.capacity() -
						 * this.recordLength) > ) { this.recordLength = recordBuffer.remaining(); }
						 */
					}
					recordBuffer.compact();
				}
				Logger.info("reading file:: " + fileToRead + " # of records:: " + numberRecordsInFile);

				synchronized (numberRecords) {
					numberRecords += numberRecordsInFile;
				}
				synchronized (keys) {
					keys.addAll(recordKeys);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String fileToRead;
		String writeToFile;
		FileHandler fileHandler;
		ArrayList<KeyMetaData> keyMetaData;
		ArrayList<SplitMetaData> splitMetaData;
		int recordLength;
	}

	
	
	
	static class RecordKey implements Comparator<RecordKey>, Comparable<RecordKey> {
		@Override
		public int compare(RecordKey left, RecordKey right) {

			for (int i = 0; i < left.KeyData.length && i < right.KeyData.length; i++) {
				int a = (left.KeyData[i] & 0xff);
				int b = (right.KeyData[i] & 0xff);
				if (a != b) {
					return a - b;
				}
			}

			return left.KeyData.length - right.KeyData.length;
		}

		@Override
		public int compareTo(RecordKey right) {
			int comp = 0;
			for (KeyMetaData key : keyMetaList) {
				this.KeyData = new byte[key.length];
				System.arraycopy(this.recordData, key.start, this.KeyData, 0, key.length);

				right.KeyData = new byte[key.length];
				System.arraycopy(right.recordData, key.start, right.KeyData, 0, key.length);
				if (key.sortType.equals("DESC")) {
					comp = compare(right, this);
				} else {
					comp = compare(this, right);
				}

				if (comp != 0) {
					break;
				}
			}
			return comp;
		}

		byte recordData[];
		byte KeyData[];
		int recordNumber;
		boolean isProcessed = false;
		boolean isSplitted = false;
		FileHandler fileHandler;
		String writeToFile;
		ArrayList<KeyMetaData> keyMetaList;

	}

	static class KeyMetaData {
		public KeyMetaData(int start, int length, String sortType) {
			this.start = start;
			this.length = length;
			this.sortType = sortType;
		}

		int start;
		int length;
		String sortType;
	}

	static class SplitMetaData implements Runnable {
		public SplitMetaData(int start, int length, String value, String splitFileName, int splitRecordLenght, boolean newLineRequired)
				throws FileNotFoundException {
			this.start = start;
			this.splitLength = length;
			this.value = value;
			this.splitFileName = splitFileName;
			this.splitRecordLenght = splitRecordLenght;

			valueBytes = new byte[value.split(" #OR ").length][];

			if (EBCDIC) {
				try {
					for (int i = 0; i < value.split(" #OR ").length; i++) {
						valueBytes[i] = value.split(" #OR ")[i].getBytes("cp500");
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			} else {
				for (int i = 0; i < value.split(" #OR ").length; i++) {
					valueBytes[i] = value.split(" #OR ")[i].getBytes();
				}

			}

			this.splitFileObj = new File(splitFileName);
			if (splitFileObj.exists()) {
				splitFileObj.delete();
			}

			this.newLineRequired = newLineRequired;
		}

		@Override
		public void run() {
			long totalWriteTime = 0;
			long totalReadTime = 0;
			ByteBuffer buffer = ByteBuffer.allocate(1024 * 8);
			FileChannel swc = null;
			try {
				FileOutputStream outputStrem = new FileOutputStream(splitFileObj, !this.newLineRequired);
				swc = outputStrem.getChannel();
				boolean match = true;
				for (int i = 0; i < this.splitRecords.size(); i++) {
					try {
						long b = System.currentTimeMillis();
						totalReadTime += (System.currentTimeMillis() - b);
						buffer.put(splitRecords.get(i));
						if (this.newLineRequired) {
							buffer.put("\n".getBytes());
						}
						buffer.flip();
						if (match) {
							swc.write(buffer);
						}
						buffer.clear();
						totalWriteTime += (System.currentTimeMillis() - b);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				swc.close();
				outputStrem.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			Logger.info("splitting file:: " + splitFileName + " totalReadTime=" + totalReadTime + " totalWriteTime=" + totalWriteTime);
		}

		int start;
		int splitLength;
		String value;
		String splitFileName;
		File splitFileObj;
		// for splitting single file based on multiple conditions
		byte valueBytes[][];
		ArrayList<byte[]> splitRecords = new ArrayList<byte[]>();
		int splitRecordLenght;
		boolean newLineRequired;

		public ArrayList<byte[]> getSplitRecords() {
			return splitRecords;
		}

		public void setSplitRecords(ArrayList<byte[]> splitRecords) {
			this.splitRecords = splitRecords;
		}

	}

	static class FileHandler {
		public FileHandler(RandomAccessFile file) {
			this.file = file;
			this.channel = file.getChannel();
		}

		synchronized int readRecord(int recordNumber, int recordLength, byte record[], int offset) throws IOException {
			file.seek(((long) recordNumber) * ((long) recordLength));
			return file.read(record, offset, recordLength);
		}

		RandomAccessFile file;
		FileChannel channel;
	}

	static ArrayList<RecordKey> keys = new ArrayList<>();
	static Integer numberRecords = 0;

	static int recordLength;
	static boolean EBCDIC;
	static boolean testEnv;
}
