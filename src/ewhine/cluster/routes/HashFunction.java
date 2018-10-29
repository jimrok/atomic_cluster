package ewhine.cluster.routes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class HashFunction {
//	private MessageDigest md5 = null;
//
//	public long hash(String key) {
//		if (md5 == null) {
//			try {
//				md5 = MessageDigest.getInstance("MD5");
//			} catch (NoSuchAlgorithmException e) {
//				throw new IllegalStateException("no md5 algorythm found");
//			}
//		}
//
//		md5.reset();
//		md5.update(key.getBytes());
//		byte[] bKey = md5.digest();
//		long res = ((long) (bKey[3] & 0xFF) << 24)
//				| ((long) (bKey[2] & 0xFF) << 16)
//				| ((long) (bKey[1] & 0xFF) << 8) | (long) (bKey[0] & 0xFF);
//		return res & 0xffffffffL;
//	}
	
	/**  
     *  MurMurHash算法，是非加密HASH算法，性能很高，  
     *  比传统的CRC32,MD5，SHA-1（这两个算法都是加密HASH算法，复杂度本身就很高，带来的性能上的损害也不可避免）  
     *  等HASH算法要快很多，而且据说这个算法的碰撞率很低.  
     *  http://murmurhash.googlepages.com/  
     */  
    public long hash(String key) {       
        ByteBuffer buf = ByteBuffer.wrap(key.getBytes());   
        int seed = 0x1234ABCD;   
        ByteOrder byteOrder = buf.order();   
        buf.order(ByteOrder.LITTLE_ENDIAN);   
        long m = 0xc6a4a7935bd1e995L;   
        int r = 47;   
        long h = seed ^ (buf.remaining() * m);   
        long k;   
        while (buf.remaining() >= 8) {   
            k = buf.getLong();   
            k *= m;   
            k ^= k >>> r;   
            k *= m;   
            h ^= k;   
            h *= m;   
        }   
        if (buf.remaining() > 0) {   
            ByteBuffer finish = ByteBuffer.allocate(8).order(   
                    ByteOrder.LITTLE_ENDIAN);   
            // for big-endian version, do this first:    
            // finish.position(8-buf.remaining());    
            finish.put(buf).rewind();   
            h ^= finish.getLong();   
            h *= m;   
        }   
        h ^= h >>> r;   
        h *= m;   
        h ^= h >>> r;   
        buf.order(byteOrder);   
        return h;   
    }   

}
