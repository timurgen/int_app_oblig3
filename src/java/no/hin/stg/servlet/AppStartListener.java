package no.hin.stg.servlet;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * <p>Web application listener
 *
 * @author Timur Samkharadze
 */
public class AppStartListener implements ServletContextListener {

    private JmDNS jmdns;

    /**
     * <p>Application server will execute this mthod automatically after
     * application deploy.
     *
     * @param sce ServletContext event
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {//create and register multcast DNS service
            Enumeration<NetworkInterface> cards = NetworkInterface.getNetworkInterfaces();
            while (cards.hasMoreElements()) {
                NetworkInterface current = cards.nextElement();
                if (current.isLoopback() || current.isVirtual() || current.isPointToPoint() || !current.isUp() || !current.supportsMulticast()) {
                    break;
                }
                Enumeration<InetAddress> ipes = current.getInetAddresses();
                while (ipes.hasMoreElements()) {
                    InetAddress currentIp = ipes.nextElement();
                    jmdns = JmDNS.create(InetAddress.getByAddress(currentIp.getAddress()));
                    HashMap<String, String> params = new HashMap<String, String>(5);
                    params.put("ServiceURL", sce.getServletContext().getContextPath() + "/Oblig3Service");
                    params.put("WSDLURL", sce.getServletContext().getContextPath() + "/Oblig3Service?WSDL");
                    ServiceInfo info = ServiceInfo.create("_http._tcp.local", "WebService", 8080, 0, 0, params);
                    jmdns.registerService(info);
                    sce.getServletContext().setAttribute("message", "JmDNS service is started on " + currentIp.getHostAddress() + " succesfully");
                }

            }

        } catch (IOException ex) {
            Logger.getLogger(AppStartListener.class.getName()).log(Level.SEVERE, null, ex);
            sce.getServletContext().setAttribute("message", "JmDNS service is not started due " + ex.getMessage());
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
