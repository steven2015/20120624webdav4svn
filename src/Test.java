/**
 * 
 */
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Enumeration;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * @author Steven
 * 
 */
public class Test{
	public static void main(final String[] args) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException, UnrecoverableKeyException, KeyManagementException{
		final SSLContext context = SSLContext.getInstance("SSL");
		final KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		final KeyStore keyStore = KeyStore.getInstance("WINDOWS-MY");
		keyStore.load(null, null);
		keyFactory.init(keyStore, null);
		final TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		final KeyStore trustStore = KeyStore.getInstance("WINDOWS-ROOT");
		trustStore.load(null, null);
		trustFactory.init(trustStore);
		context.init(keyFactory.getKeyManagers(), trustFactory.getTrustManagers(), null);
		/*final HttpsURLConnection conn = (HttpsURLConnection)new URL("https://<redacted>").openConnection();
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		conn.setSSLSocketFactory(context.getSocketFactory());*/
		Enumeration<String> en = trustStore.aliases();
		while(en.hasMoreElements()){
			String aliasKey = (String)en.nextElement();
			Certificate c = trustStore.getCertificate(aliasKey);
			System.out.println("---> alias : " + aliasKey);
			if(trustStore.isKeyEntry(aliasKey)){
				Certificate[] chain = trustStore.getCertificateChain(aliasKey);
				System.out.println("---> chain length: " + chain.length);
				for(Certificate cert : chain){
					System.out.println(cert);
				}
			}
		}
	}
}
