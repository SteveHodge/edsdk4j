package edsdk.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.imageio.ImageIO;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;

import edsdk.api.CanonCamera;
import edsdk.bindings.EdSdkLibrary;
import edsdk.bindings.EdsDirectoryItemInfo;
import edsdk.bindings.EdSdkLibrary.EdsVoid;
import edsdk.bindings.EdSdkLibrary.__EdsObject;
import edsdk.bindings.EdsPropertyDesc;

/**
 * Here are some great helpers. 
 * _All_ the functions in here are not thread save, so you'll want to encapsulate them in 
 * a CanonTask and then send them to the camera, like so for instance : 
 * 
 * <pre>
 * canonCamera.executeNow( new CanonTask<Boolean>(){
 * 	 public void run(){
 * 		CanonUtils.doSomethingLikeDownloadOrWhatever(); 
 *   }
 * }
 * </pre>
 * 
 * Copyright © 2014 Hansi Raber <super@superduper.org>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 * 
 * @author hansi
 */
public class CanonUtils {
	/**
	 * Converts a bunch of bytes to a string. 
	 * This is a little different from new String( myBytes ) because
	 * byte-arrays received from C will be crazy long and just have a null-terminator
	 * somewhere in the middle. 
	 */
	public static String toString( byte bytes[] ){
		for( int i = 0; i < bytes.length; i++ ){
			if( bytes[i] == 0 ){
				return new String( bytes, 0, i ); 
			}
		}
		
		return new String( bytes ); 
	}
	
	/**
	 * Tries to find name of an error code. 
	 * 
	 * @param errorCode
	 * @return
	 */
	public static String toString( int errorCode ){
		Field[] fields = EdSdkLibrary.class.getFields();
		
		for( Field field : fields ){
			try {
				if( field.getType().toString().equals( "int" ) && field.getInt( EdSdkLibrary.class ) == errorCode ){
					if( field.getName().startsWith( "EDS_" ) ){
						return field.getName(); 
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return "unknown error code"; 
	}
	

	public static String propertyIdToString(long property) {
		Field[] fields = EdSdkLibrary.class.getFields();
		
		for( Field field : fields ){
			try {
				if( field.getType().toString().equals( "int" ) && field.getInt( EdSdkLibrary.class ) == property ){
					if( field.getName().startsWith( "kEdsPropID_" ) ){
						return field.getName(); 
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return "unknown error code"; 
	}
	
	public static String getQualityDescriptionForShortDescription( String str ){
		/* Jpeg Only */
		if("LJ".equals( str ) ) return "Jpeg Large"; 
		if("M1J".equals( str ) ) return "Jpeg Middle1"; 
		if("M2J".equals( str ) ) return "Jpeg Middle2"; 
		if("SJ".equals( str ) ) return "Jpeg Small"; 
		if("LJF".equals( str ) ) return "Jpeg Large Fine"; 
		if("LJN".equals( str ) ) return "Jpeg Large Normal"; 
		if("MJF".equals( str ) ) return "Jpeg Middle Fine"; 
		if("MJN".equals( str ) ) return "Jpeg Middle Normal"; 
		if("SJF".equals( str ) ) return "Jpeg Small Fine"; 
		if("SJN".equals( str ) ) return "Jpeg Small Normal"; 
		if("S1JF".equals( str ) ) return "Jpeg Small1 Fine"; 
		if("S1JN".equals( str ) ) return "Jpeg Small1 Normal"; 
		if("S2JF".equals( str ) ) return "Jpeg Small2"; 
		if("S3JF".equals( str ) ) return "Jpeg Small3"; 

		/* RAW + Jpeg */
		if("LR".equals( str ) ) return "RAW"; 
		if("LRLJF".equals( str ) ) return "RAW + Jpeg Large Fine"; 
		if("LRLJN".equals( str ) ) return "RAW + Jpeg Large Normal"; 
		if("LRMJF".equals( str ) ) return "RAW + Jpeg Middle Fine"; 
		if("LRMJN".equals( str ) ) return "RAW + Jpeg Middle Normal"; 
		if("LRSJF".equals( str ) ) return "RAW + Jpeg Small Fine"; 
		if("LRSJN".equals( str ) ) return "RAW + Jpeg Small Normal"; 
		if("LRS1JF".equals( str ) ) return "RAW + Jpeg Small1 Fine"; 
		if("LRS1JN".equals( str ) ) return "RAW + Jpeg Small1 Normal"; 
		if("LRS2JF".equals( str ) ) return "RAW + Jpeg Small2"; 
		if("LRS3JF".equals( str ) ) return "RAW + Jpeg Small3"; 

		if("LRLJ".equals( str ) ) return "RAW + Jpeg Large"; 
		if("LRM1J".equals( str ) ) return "RAW + Jpeg Middle1"; 
		if("LRM2J".equals( str ) ) return "RAW + Jpeg Middle2"; 
		if("LRSJ".equals( str ) ) return "RAW + Jpeg Small"; 

		/* MRAW(SRAW1) + Jpeg */
		if("MR".equals( str ) ) return "MRAW(SRAW1)"; 
		if("MRLJF".equals( str ) ) return "MRAW(SRAW1) + Jpeg Large Fine"; 
		if("MRLJN".equals( str ) ) return "MRAW(SRAW1) + Jpeg Large Normal"; 
		if("MRMJF".equals( str ) ) return "MRAW(SRAW1) + Jpeg Middle Fine"; 
		if("MRMJN".equals( str ) ) return "MRAW(SRAW1) + Jpeg Middle Normal"; 
		if("MRSJF".equals( str ) ) return "MRAW(SRAW1) + Jpeg Small Fine"; 
		if("MRSJN".equals( str ) ) return "MRAW(SRAW1) + Jpeg Small Normal"; 
		if("MRS1JF".equals( str ) ) return "MRAW(SRAW1) + Jpeg Small1 Fine"; 
		if("MRS1JN".equals( str ) ) return "MRAW(SRAW1) + Jpeg Small1 Normal"; 
		if("MRS2JF".equals( str ) ) return "MRAW(SRAW1) + Jpeg Small2"; 
		if("MRS3JF".equals( str ) ) return "MRAW(SRAW1) + Jpeg Small3"; 

		if("MRLJ".equals( str ) ) return "MRAW(SRAW1) + Jpeg Large"; 
		if("MRM1J".equals( str ) ) return "MRAW(SRAW1) + Jpeg Middle1"; 
		if("MRM2J".equals( str ) ) return "MRAW(SRAW1) + Jpeg Middle2"; 
		if("MRSJ".equals( str ) ) return "MRAW(SRAW1) + Jpeg Small"; 

		/* SRAW(SRAW2) + Jpeg */
		if("SR".equals( str ) ) return "SRAW(SRAW2)"; 
		if("SRLJF".equals( str ) ) return "SRAW(SRAW2) + Jpeg Large Fine"; 
		if("SRLJN".equals( str ) ) return "SRAW(SRAW2) + Jpeg Large Normal"; 
		if("SRMJF".equals( str ) ) return "SRAW(SRAW2) + Jpeg Middle Fine"; 
		if("SRMJN".equals( str ) ) return "SRAW(SRAW2) + Jpeg Middle Normal"; 
		if("SRSJF".equals( str ) ) return "SRAW(SRAW2) + Jpeg Small Fine"; 
		if("SRSJN".equals( str ) ) return "SRAW(SRAW2) + Jpeg Small Normal"; 
		if("SRS1JF".equals( str ) ) return "SRAW(SRAW2) + Jpeg Small1 Fine"; 
		if("SRS1JN".equals( str ) ) return "SRAW(SRAW2) + Jpeg Small1 Normal"; 
		if("SRS2JF".equals( str ) ) return "SRAW(SRAW2) + Jpeg Small2"; 
		if("SRS3JF".equals( str ) ) return "SRAW(SRAW2) + Jpeg Small3"; 

		if("SRLJ".equals( str ) ) return "SRAW(SRAW2) + Jpeg Large"; 
		if("SRM1J".equals( str ) ) return "SRAW(SRAW2) + Jpeg Middle1"; 
		if("SRM2J".equals( str ) ) return "SRAW(SRAW2) + Jpeg Middle2"; 
		if("SRSJ".equals( str ) ) return "SRAW(SRAW2) + Jpeg Small"; 
		
		return "Unknown Image Quality"; 
	}
	
	/**
	 * Finds the size of a class
	 * Use only with JNA stuff! 
	 */
	public static int sizeof( Object o ){
		int size = 0;
		for(Field field : o.getClass().getDeclaredFields()) {
			Class<?> fieldtype = field.getType();
			if( fieldtype.equals( NativeLong.class ) ){
				size += NativeLong.SIZE; 
			}
			else{
				System.out.println( "unknown field type: " + field ); 
			}
			// sofern nur char[] m�glich, keinerlei weitere Pr�fung, ansonsten typenpr�fung anbauen
			//char[] sub =(char[]) field.get(o);
			//size+=sub.length;
		}
		
		return size;
	}

	
	/**
	 * Finds the filename for a directory item
	 * @param directoryItem The item you want to download
	 * @return Either null, or the filename of the item 
	 */
	public static EdsDirectoryItemInfo getDirectoryItemInfo( __EdsObject directoryItem ){
		int err = EdSdkLibrary.EDS_ERR_OK;
		EdsDirectoryItemInfo dirItemInfo = new EdsDirectoryItemInfo();

		err = CanonCamera.EDSDK.EdsGetDirectoryItemInfo(directoryItem, dirItemInfo).intValue();
		if (err == EdSdkLibrary.EDS_ERR_OK) {
			return dirItemInfo;  
		}
		else{
			return null; 
		}
	}
	
	/**
	 * Downloads an image and saves it somewhere
	 * @param directoryItem The item you want to download
	 * @param destination A path in the filesystem where you want to save the file. Can also be null or a directory. In case of null the temp directory will be used, in case of a directory the file name of the item will be used. 
	 * @param deleteAfterDownload Should the image be deleted right after successful download  
	 * @return Either null, or the location the file was ultimately saved to on success. 
	 */
	public static File download( __EdsObject directoryItem, File destination, boolean deleteAfterDownload ){
		int err = EdSdkLibrary.EDS_ERR_OK;
		__EdsObject[] stream = new __EdsObject[1]; 
		EdsDirectoryItemInfo dirItemInfo = new EdsDirectoryItemInfo();

		boolean success = false;

		long timeStart = System.currentTimeMillis();

		err = CanonCamera.EDSDK.EdsGetDirectoryItemInfo(directoryItem, dirItemInfo).intValue();
		if (err == EdSdkLibrary.EDS_ERR_OK) {
			if( destination == null ){
				destination = new File( System.getProperty("java.io.tmpdir") ); 
			}
			if (destination.isDirectory()) {
				destination = new File(destination, toString( dirItemInfo.szFileName ) );
			}
			
			destination.getParentFile().mkdirs(); 
			
			System.out.println("Downloading image "
					+ toString(dirItemInfo.szFileName) + " to "
					+ destination.getAbsolutePath());

			err = CanonCamera.EDSDK.EdsCreateFileStream(
					ByteBuffer.wrap( Native.toByteArray( destination.getAbsolutePath() ) ), 
					EdSdkLibrary.EdsFileCreateDisposition.kEdsFileCreateDisposition_CreateAlways,
					EdSdkLibrary.EdsAccess.kEdsAccess_ReadWrite, 
					stream
			).intValue();
		}

		if(err == EdSdkLibrary.EDS_ERR_OK ){
			err = CanonCamera.EDSDK.EdsDownload( directoryItem, dirItemInfo.size, stream[0] ).intValue();
		}

		if( err == EdSdkLibrary.EDS_ERR_OK ){
//			System.out.println( "Image downloaded in " +  ( System.currentTimeMillis() - timeStart ) );

			err = CanonCamera.EDSDK.EdsDownloadComplete( directoryItem ).intValue();
			if( deleteAfterDownload ){
//				System.out.println( "Image deleted" );
				CanonCamera.EDSDK.EdsDeleteDirectoryItem( directoryItem );
			}
			
			success = true;
		}
		
		if( stream[0] != null ){
			CanonCamera.EDSDK.EdsRelease( stream[0] ); 
		}
		
		return success? destination : null;
	}
	
	/*public static long getPropertySize( __EdsObject ref, long property ){
		IntBuffer type = IntBuffer.allocate( 1 ); 
		NativeLongByReference number = new NativeLongByReference( new NativeLong( 1 ) ); 
		NativeLong res = CanonCamera.EDSDK.EdsGetPropertySize( ref, new NativeLong( property ), new NativeLong( 0 ), type, number );
		
		System.out.println( "A=" + res.intValue() ); 
		System.out.println( "B=" + number.getValue().intValue() );
		return 0; 
	}*/
	
	
	
	public static int setPropertyData( __EdsObject ref, long property, long param, int size, EdsVoid data ){
		return CanonCamera.EDSDK.EdsSetPropertyData( ref, new NativeLong( property ), new NativeLong( param ), new NativeLong( size ), data ).intValue(); 
	}
	
	public static int setPropertyData( __EdsObject ref, long property, long value ){
		NativeLongByReference number = new NativeLongByReference( new NativeLong( value ) ); 
		EdsVoid data = new EdsVoid( number.getPointer() ); 

		return setPropertyData( ref, property, 0, NativeLong.SIZE, data ); 
	}
	
	public static int getPropertyData( __EdsObject ref, long property, long param, int size, EdsVoid data ){
		return CanonCamera.EDSDK.EdsGetPropertyData( ref, new NativeLong( property ), new NativeLong( param ), new NativeLong( size ), data ).intValue(); 
	}
	
	public static int getPropertyData( __EdsObject ref, long property ){
		NativeLongByReference number = new NativeLongByReference( new NativeLong( 1 ) ); 
		EdsVoid data = new EdsVoid( number.getPointer() ); 

		getPropertyData( ref, property, 0, NativeLong.SIZE, data );
		
		return number.getValue().intValue(); 
	}
	
	public static boolean beginLiveView( __EdsObject camera ){
		int err = EdSdkLibrary.EDS_ERR_OK;

		NativeLongByReference number = new NativeLongByReference( new NativeLong( 1 ) ); 
		EdsVoid data = new EdsVoid( number.getPointer() ); 
		err = setPropertyData( camera, EdSdkLibrary.kEdsPropID_Evf_Mode, 0, NativeLong.SIZE, data ); 
		if( err != EdSdkLibrary.EDS_ERR_OK ){
			System.err.println( "Couldn't start live view, error=" + err + ", " + toString( err ) ); 
			return false; 
		}
		
		//TODO:delete! 
		//getPropertyData( camera, EdSdkLibrary.kEdsPropID_Evf_Mode, 0, NativeLong.SIZE, data ); 
		//System.out.println( "===" + number.getValue() ); 
		
		number = new NativeLongByReference( new NativeLong( EdSdkLibrary.EdsEvfOutputDevice.kEdsEvfOutputDevice_PC ) ); 
		data = new EdsVoid( number.getPointer() ); 
		err = setPropertyData( camera, EdSdkLibrary.kEdsPropID_Evf_OutputDevice, 0, NativeLong.SIZE, data ); 
		if( err != EdSdkLibrary.EDS_ERR_OK ){
			System.err.println( "Couldn't start live view, error=" + err + ", " + toString( err ) ); 
			return false; 
		}
		
		
		return true; 
	}
	
	public static boolean endLiveView( __EdsObject camera ){
		int err = EdSdkLibrary.EDS_ERR_OK;

		NativeLongByReference number = new NativeLongByReference( new NativeLong( 0 ) ); 
		EdsVoid data = new EdsVoid( number.getPointer() ); 
		err = setPropertyData( camera, EdSdkLibrary.kEdsPropID_Evf_Mode, 0, NativeLong.SIZE, data ); 
		if( err != EdSdkLibrary.EDS_ERR_OK ){
			// System.err.println( "Couldn't end live view, error=" + err + ", " + toString( err ) ); 
			return false; 
		}
		
		number = new NativeLongByReference( new NativeLong( EdSdkLibrary.EdsEvfOutputDevice.kEdsEvfOutputDevice_TFT ) ); 
		data = new EdsVoid( number.getPointer() ); 
		err = setPropertyData( camera, EdSdkLibrary.kEdsPropID_Evf_OutputDevice, 0, NativeLong.SIZE, data ); 
		if( err != EdSdkLibrary.EDS_ERR_OK ){
			// System.err.println( "Couldn't end live view, error=" + err + ", " + toString( err ) ); 
			return false; 
		}
		
		
		return true; 
	}
	
	public static boolean isLiveViewEnabled( __EdsObject camera ){
		return getPropertyData( camera , EdSdkLibrary.kEdsPropID_Evf_Mode ) == 1;
	}
	
	public static BufferedImage downloadLiveViewImage( __EdsObject camera ){
		int err = EdSdkLibrary.EDS_ERR_OK;
		//EdsStreamRef stream = NULL;
		//EdsEvfImageRef = NULL;
		__EdsObject stream[] = new __EdsObject[1]; 
		__EdsObject image[] = new __EdsObject[1]; 
		
		// Create memory stream.
		err = CanonCamera.EDSDK.EdsCreateMemoryStream( new NativeLong( 0 ), stream ).intValue(); 
		if( err != EdSdkLibrary.EDS_ERR_OK ){
			System.err.println( "Failed to download life view image, memory stream couldn't be created: code=" + err + ", " + toString( err ) ); 
			release( image[0], stream[0] ); 
			return null; 
		}

		err = CanonCamera.EDSDK.EdsCreateEvfImageRef( stream[0], image ).intValue(); 
		if( err != EdSdkLibrary.EDS_ERR_OK ){
			System.err.println( "Failed to download life view image, image ref couldn't be created: code=" + err + ", " + toString( err ) ); 
			release( image[0], stream[0] ); 
			return null; 
		}

		// Now try to follow the guidelines from 
		// http://tech.groups.yahoo.com/group/CanonSDK/message/1225
		// instead of what the edsdk example has to offer! 
		
		// Download live view image data.
		err = CanonCamera.EDSDK.EdsDownloadEvfImage( camera, image[0] ).intValue(); 
		if( err != EdSdkLibrary.EDS_ERR_OK ){
			// System.err.println( "Failed to download life view image, code=" + err + ", " + toString( err ) ); 
			release( image[0], stream[0] ); 
			return null; 
		}
		
//
//		// Get the incidental data of the image.
//		NativeLongByReference zoom = new NativeLongByReference();
//		EdsVoid data = new EdsVoid(); 
//		err = getPropertyData( image[0], CanonSDK.kEdsPropID_Evf_ZoomPosition, 0, NativeLong.SIZE, data ); 
//		if( err != CanonSDK.EDS_ERR_OK ){
//			System.err.println( "Failed to download life view image, zoom value wasn't read: code=" + err + ", " + toString( err ) ); 
//			return false; 
//		}
//
//		// Get the focus and zoom border position
//		EdsPoint point = new EdsPoint();
//		data = new EdsVoid( point.getPointer() ); 
//		err = getPropertyData( image[0], CanonSDK.kEdsPropID_Evf_ZoomPosition, 0 , sizeof( point ), data );
//		if( err != CanonSDK.EDS_ERR_OK ){
//			System.err.println( "Failed to download life view image, focus point wasn't read: code=" + err + ", " + toString( err ) ); 
//			return false; 
//		}
//		
//		return true; 
		
		NativeLongByReference length = new NativeLongByReference(); 
		err = CanonCamera.EDSDK.EdsGetLength( stream[0], length ).intValue(); 
		if( err != EdSdkLibrary.EDS_ERR_OK ){
			System.err.println( "Failed to download life view image, failed to read stream length: code=" + err + ", " + toString( err ) ); 
			release( image[0], stream[0] ); 
			return null; 
		}
		
		PointerByReference ref = new PointerByReference(); 
		err = CanonCamera.EDSDK.EdsGetPointer( stream[0], ref ).intValue(); 

		long address = ref.getPointer().getNativeLong( 0 ).longValue(); 
		Pointer pp = new Pointer( address ); 
		byte data[] = pp.getByteArray( 0, length.getValue().intValue() ); 
		try {
			BufferedImage img = ImageIO.read( new ByteArrayInputStream( data ) );
			return img; 
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			release( image[0], stream[0] ); 
		}
		
		return null; 
	}
	
	public static EdsPropertyDesc getPropertyDesc( __EdsObject camera, long inPropertyID ){
		EdsPropertyDesc.ByReference desc = new EdsPropertyDesc.ByReference(); 
		
		NativeLong err = CanonCamera.EDSDK.EdsGetPropertyDesc( camera, new NativeLong(inPropertyID), desc ); 
		if( err.longValue() != EdSdkLibrary.EDS_ERR_OK ){
			System.out.println( "EdsGetPropertyDesc Error#" + err.longValue() + ": " + CanonUtils.toString( err.intValue() ) ); 
		}
		return desc; 
	}
	
	public static LinkedHashMap<String, Integer> listImageQualities( __EdsObject camera ){
		LinkedHashMap<String,Integer> result = new LinkedHashMap<String,Integer>(); 
		EdsPropertyDesc desc = CanonUtils.getPropertyDesc( camera, EdSdkLibrary.kEdsPropID_ImageQuality ); 
		Field[] fields = EdSdkLibrary.EdsImageQuality.class.getFields(); 
		
		next:
		for( int i = 0; i < desc.numElements.intValue(); i++ ){
			int value = desc.propDesc[i].intValue(); 
			for( Field field : fields ){
				try {
					if( field.getType().toString().equals( "int" ) && field.getInt( EdSdkLibrary.class ) == value ){
						if( field.getName().startsWith( "EdsImageQuality_" ) ){
							result.put( field.getName().substring( 16 ), value ); 
							continue next; 
						}
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
			
			result.put( "Unknown:" + value, value ); 
		}

		return result; 
	}
	
	public static void release( __EdsObject ... objects ){
		for( __EdsObject obj : objects ){
			if( obj != null ){
				CanonCamera.EDSDK.EdsRelease( obj ); 
			}
		}
	}
}
