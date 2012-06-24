/**
 * 
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * @author Steven
 * 
 */
public class Test{
	public static void main(final String[] args) throws IOException{
		//testConnection();
		//testSocket();
		testHttpClient();
	}
	public static void testWindowsCert() throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException, UnrecoverableKeyException, KeyManagementException{
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
		final HttpsURLConnection conn = (HttpsURLConnection)new URL("https://<redacted>").openConnection();
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		conn.setSSLSocketFactory(context.getSocketFactory());
		final Enumeration<String> en = trustStore.aliases();
		while(en.hasMoreElements()){
			final String aliasKey = en.nextElement();
			final Certificate c = trustStore.getCertificate(aliasKey);
			System.out.println("---> alias : " + aliasKey);
			System.out.println(c);
			if(trustStore.isKeyEntry(aliasKey)){
				final Certificate[] chain = trustStore.getCertificateChain(aliasKey);
				System.out.println("---> chain length: " + chain.length);
				for(final Certificate cert : chain){
					System.out.println(cert);
				}
			}
		}
	}
	public static void testConnection() throws IOException{
		final URL url = new URL("http://hk.yahoo.com");
		final URLConnection urlConn = url.openConnection();
		final HttpURLConnection conn = (HttpURLConnection)urlConn;
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		final OutputStream os = conn.getOutputStream();
		final OutputStreamWriter osw = new OutputStreamWriter(os);
		//osw.append("POST");
		osw.flush();
		osw.close();
		os.flush();
		System.out.println(conn.getResponseCode());
		final InputStream is = conn.getInputStream();
		final InputStreamReader isr = new InputStreamReader(is);
		final BufferedReader br = new BufferedReader(isr);
		String line = null;
		while((line = br.readLine()) != null){
			System.out.println(line);
		}
		br.close();
		isr.close();
		conn.connect();
	}
	public static void testSocket() throws IOException{
		final Socket s = new Socket("hk.yahoo.com", 80);
		final OutputStream os = s.getOutputStream();
		final OutputStreamWriter osw = new OutputStreamWriter(os);
		osw.append("GET / HTTP/1.1\r\nHost: hk.yahoo.com\r\nUser-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:15.0) Gecko/20120623 Firefox/15.0a2\r\n\r\n");
		osw.flush();
		final InputStream is = s.getInputStream();
		final InputStreamReader isr = new InputStreamReader(is);
		final BufferedReader br = new BufferedReader(isr);
		String line = null;
		int contentLength = 0;
		while((line = br.readLine()) != null){
			System.out.println(line);
			if("".equals(line)){
				break;
			}else if(line.startsWith("Content-Length:")){
				contentLength = Integer.parseInt(line.substring("Content-Length:".length()));
			}
		}
		System.out.println(contentLength);
		br.close();
		isr.close();
		osw.close();
		s.close();
	}
	public static void testHttpClient() throws IllegalStateException, IOException{
		final HttpClient httpclient = new DefaultHttpClient();
		final HttpGet httpget = new HttpGet("http://hk.yahoo.com");
		final HttpResponse response = httpclient.execute(httpget);
		System.out.println(response.getStatusLine());
		final HttpEntity entity = response.getEntity();
		if(entity != null){
			final InputStream instream = entity.getContent();
			final BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
			System.out.println(reader.readLine());
			instream.close();
			httpclient.getConnectionManager().shutdown();
		}
	}
}
