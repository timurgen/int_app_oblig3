package no.hin.stg.servlet;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * <p>Web application listener, reagerer på hendelser som applikasjonsstart og applikasjonsstop
 *
 * @author Timur Samkharadze
 */
public class AppStartListener implements ServletContextListener {

    private JmDNS jmdns; //zero conf provider

    /**
     * <p>Application server will execute this mthod automatically after
     * application deploy.
     *
     * @param sce ServletContext event
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        List<String> start_messages = new ArrayList<String>(10);
        try {//create and register multcast DNS service
            Enumeration<NetworkInterface> cards = NetworkInterface.getNetworkInterfaces();
            
            while (cards.hasMoreElements()) {
                NetworkInterface current = cards.nextElement();
                if (current.isLoopback() || current.isVirtual() || current.isPointToPoint() || !current.isUp() || !current.supportsMulticast()) {
                    continue;
                }
                Enumeration<InetAddress> ipes = current.getInetAddresses();
                while (ipes.hasMoreElements()) {
                    InetAddress currentIp = ipes.nextElement();
                    jmdns = JmDNS.create(InetAddress.getByAddress(currentIp.getAddress()));
                    HashMap<String, String> params = new HashMap<String, String>(5);
                    params.put("ServiceURL", sce.getServletContext().getContextPath() + "/Oblig3Service");
                    params.put("WSDLURL", sce.getServletContext().getContextPath() + "/Oblig3Service?WSDL");
                    params.put("resturl",sce.getServletContext().getContextPath() +"/webresources/");
                    ServiceInfo info = ServiceInfo.create("_http._tcp.local", "WebService", 8080, 0, 0, params);
                    jmdns.registerService(info);
                    start_messages.add("JmDNS service is started on " + currentIp.getHostAddress() + " succesfully");
                }
            }
//            //Service location protocoll
//            // get Advertiser instance
//            Advertiser advertiser = ServiceLocationManager.getAdvertiser(new Locale("en"));
//            // the service has lifetime 600, that means it will only persist for ti minutes
//            ServiceURL myService = new ServiceURL("service:test:myService://"+sce.getServletContext().getContextPath() + "/Oblig3Service", 600);
//            // some attributes for the service
//            @SuppressWarnings("UseOfObsoleteCollectionType")
//            Hashtable<String, String> attributes = new Hashtable<String, String>(2);
//            attributes.put("ServiceURL", sce.getServletContext().getContextPath() + "/Oblig3Service");
//            attributes.put("WSDLURL", sce.getServletContext().getContextPath() + "/Oblig3Service?WSDL");
//            advertiser.register(myService, attributes);

        } catch (IOException ex) {
            Logger.getLogger(AppStartListener.class.getName()).log(Level.SEVERE, null, ex);
            start_messages.add("JmDNS service is not started due " + ex.getMessage());
        } 
//        catch (ServiceLocationException ex) {
//            Logger.getLogger(AppStartListener.class.getName()).log(Level.SEVERE, null, ex);
//            start_messages.add("JmDNS service is not started due " + ex.getMessage());
//        } 
        finally {
            sce.getServletContext().setAttribute("messages", start_messages);
        }
    }

    /**
     * <p>Application server will execute this mthod automatically before
     * application undeploy
     *
     * @param sce
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        jmdns.unregisterAllServices();
    }
}
