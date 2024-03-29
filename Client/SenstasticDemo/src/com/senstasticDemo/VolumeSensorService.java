package com.senstasticDemo;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.senstastic.Logger;
import com.senstastic.SensorService;

public class VolumeSensorService extends SensorService
{	
	// Overridden methods.
	
	protected String getSensorKind()
	{
		return "volume";
	}
	
	protected int getIntervalInSeconds()
	{
		return 15;
	}
	
	protected void sense()
	{	
		try
		{
			// Set the recording length.
			double recordLengthInSeconds = 5.0;
			double initializationTimeoutInSeconds = 5.0; 
			double initializationPollIntervalInSeconds = 0.1; 
			
			// Initialize the audio recorder.
			int sampleRateInHz = 44100;
	        int sampleLengthInShorts = AudioRecord.getMinBufferSize(sampleRateInHz, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
	        AudioRecord audioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRateInHz, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, sampleLengthInShorts);
	        
	        // Wait until the audio recorder has acquired the hardware resources and is initialized or timeout.
			long initializationPollIntervalInMillis = (long)(initializationPollIntervalInSeconds * 1000);
	        double initializationWaitTime = 0.0;
	        while (true)
	        {
	        	// If the audio recorder is ready, stop waiting.
	        	if (audioRecorder.getState() == AudioRecord.STATE_INITIALIZED)
	        		break;

	        	// Wait.
	        	Thread.sleep(initializationPollIntervalInMillis);
	        	
	        	// Increment the wait time. If we've timed out, throw an exception.
	        	initializationWaitTime += initializationPollIntervalInSeconds;
	        	if (initializationWaitTime >= initializationTimeoutInSeconds)
	        		throw new Exception("Could not acquire audio recording hardware resources in time!");
	        }
	        
	        // Determine the number of samples to take. Each sample fills a buffer of length sampleLength.
	        double sampleLengthInSec = (double)sampleLengthInShorts / (double)sampleRateInHz;
	        int numSamples = (int)Math.ceil(recordLengthInSeconds / sampleLengthInSec);
	        
            // Start recording.
	        audioRecorder.startRecording();
	        
	        // Take audio samples and keep track of the maximum short value throughout all of the samples.
	        short max = Short.MIN_VALUE;
	        for (int s = 0; s < numSamples; s++)
	        {
		        // Read some audio data.
		        short[] sampleData = new short[sampleLengthInShorts];
		        int numShortsRead = audioRecorder.read(sampleData, 0, sampleLengthInShorts);
	        	
		        if (numShortsRead < 0)
		        	throw new Exception("Could not take an audio sample!");
		        
		        // Update the maximum.
		        for (int i = 0; i < numShortsRead; i++)
		        {
		        	if (sampleData[i] > max)
		        		max = sampleData[i];
		        }
	        }
	        
	        // Stop recording.
	        audioRecorder.stop();
			
	        // Finish sensing and create a measurement with the maximum volume data, which will eventually be pushed to the server by the Senstastic engine.
			finishSensing(max);
		}
		catch(Exception e)
		{
			Logger.e(e.getMessage());
			
			// Finish sensing without creating a measurement or sending any results from this sensing session to the server.
			finishSensing();
		}

	}
}
