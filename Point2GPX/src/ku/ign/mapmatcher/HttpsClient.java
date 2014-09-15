package ku.ign.mapmatcher;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.io.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class HttpsClient{

	// http://stackoverflow.com/questions/20422649/how-to-write-image-or-file-to-httpsurlconnection

	String app_id = "01e8761c";
	String app_key = "5c989d0272b48a9b2cbefd19d23d95e9";
	String file = "/Users/besn/Documents/workspace/Point2GPX/out/point-raw-0.gpx";
	String requestURL = "https://test.roadmatching.com/rest/mapmatch";
	String certfile = "jssecacerts";

	public static void main(String[] args)
	{
		new HttpsClient().testIt();
	}

	private void testIt(){

		String https_url = requestURL;
		URL url;
		
		SSLSocketFactory socketFactory = null;
		
		try {
			
			try {
				socketFactory = createSSLContext().getSocketFactory();
			} catch (UnrecoverableKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CertificateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			url = new URL(https_url);
			HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
			conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setSSLSocketFactory(socketFactory);

			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			
			conn.addRequestProperty("app_id", app_id);
			conn.addRequestProperty("app_key", app_key);
			
			
			// dump all cert info
			// print_https_cert(con);

			//dump all the content
			print_content(conn);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void print_https_cert(HttpsURLConnection con){

		if(con!=null){

			try {

				System.out.println("Response Code : " + con.getResponseCode());
				System.out.println("Cipher Suite : " + con.getCipherSuite());
				System.out.println("\n");

				Certificate[] certs = con.getServerCertificates();
				for(Certificate cert : certs){
					System.out.println("Cert Type : " + cert.getType());
					System.out.println("Cert Hash Code : " + cert.hashCode());
					System.out.println("Cert Public Key Algorithm : " 
							+ cert.getPublicKey().getAlgorithm());
					System.out.println("Cert Public Key Format : " 
							+ cert.getPublicKey().getFormat());
					System.out.println("\n");
				}

			} catch (SSLPeerUnverifiedException e) {
				e.printStackTrace();
			} catch (IOException e){
				e.printStackTrace();
			}

		}

	}

	private void print_content(HttpsURLConnection con){
		if(con!=null){

			try {

				System.out.println("****** Content of the URL ********");			
				BufferedReader br = 
						new BufferedReader(
								new InputStreamReader(con.getInputStream()));

				String input;

				while ((input = br.readLine()) != null){
					System.out.println(input);
				}
				br.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
	
	private final SSLContext createSSLContext()
            throws NoSuchAlgorithmException, KeyStoreException,
            CertificateException, IOException,
            UnrecoverableKeyException, KeyManagementException {


        KeyStore keyStore = KeyStore.getInstance("PKCS12");

        FileInputStream fis = null;
        try {
            fis = new FileInputStream("keystore_file");
        } catch (Exception ex) {
            throw new IOException("not found keystore file:");
        }
        try{
            keyStore.load(fis, "blab".toCharArray());
        }finally {
            // IOUtils.closeQuietly(fis);
        }
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        FileInputStream in = new FileInputStream("cert");
        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(null);
        try {
            X509Certificate cacert = (X509Certificate) cf.generateCertificate(in);
            trustStore.setCertificateEntry("alias", cacert);
        } finally {
            // IOUtils.closeQuietly(in);
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(trustStore);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(keyStore, "pwd".toCharArray());

        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
        return sslContext;
    }

}