package no.hin.stg.intapp.oblig3;

import com.sun.xml.wss.impl.misc.Base64;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.xml.ws.WebServiceRef;
import no.hin.stg.intapp.oblig3.del2.FullerDataX0020FortuneX0020Cookie;

/**
 *
 * @author Timur Samkharadze
 * <p>Dette er class som implementerer web-service og inneholder alle oppgavedeler.
 * 1) Autentisering mot Active Directory
 * 2) Client til en annen web-service
 * 3) byte-counter
 */
@WebService(serviceName = "Oblig3Service")
public class Obig3Service {

    private static final Logger LOG = Logger.getLogger(Obig3Service.class.getName());
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/www.fullerdata.com/FortuneCookie/FortuneCookie.asmx.wsdl")
    private FullerDataX0020FortuneX0020Cookie service;

    /**
     * <p>Deloppgave 1. Autentiserer bruker mot Active Directory på HIN
     *
     * @param name brukernavn
     * @param password passord
     * @return info ifølge oppgave
     */
    @WebMethod(operationName = "autentificate")
    public List<String> autentificate(@WebParam(name = "name") String name, @WebParam(name = "password") String password) {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Hashtable<String, Object> env = new Hashtable<String, Object>(11);
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://hin-dc1.hin.no:389");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "HIN\\" + name);
        env.put(Context.SECURITY_CREDENTIALS, password);
        try {
            //logger på
            DirContext ctx = new InitialDirContext(env);
            LOG.log(Level.INFO, "Connection successfull");

            //plukker ut nødvendig info
            String[] requiredInfo = {
                "sn",
                "givenname",
                "mail"};
            SearchControls ctrls = new SearchControls();
            ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            ctrls.setReturningAttributes(requiredInfo);
            //sender forespørsel
            NamingEnumeration<SearchResult> answer = ctx.search("DC=hin,DC=no", "sAMAccountName=" + name, ctrls);
            //leser svar
            if (answer.hasMore()) {
                LOG.log(Level.INFO, "got info om {0}", name);
                List<String> userInfo = new ArrayList<String>(3);
                Attributes attrs = answer.next().getAttributes();
                userInfo.add("Username: " + name); //siden det står i oppgaven å returnere brukernavn
                userInfo.add("Name: " + (String) attrs.get("givenname").get());
                userInfo.add("Surname: " + (String) attrs.get("sn").get());
                userInfo.add("email: " + (String) attrs.get("mail").get());
                return userInfo;
            } else {
                LOG.log(Level.WARNING, "information not found");
                return new ArrayList<String>(0); //unngå nullpointer exception 
            }
        } catch (javax.naming.AuthenticationException ex) {//problemer med autentisering
            LOG.log(Level.SEVERE, null, ex);
            List<String> failInfo = new ArrayList<String>(1);
            String message = ex.getMessage();
            message = message.replace("[", "");//ellers kommer det exception 
            message = message.replace("]", "");
            StringTokenizer st = new StringTokenizer(message, ",");
            if (st.hasMoreTokens()) {//leser feilmelding
                failInfo.add("fail message: " + st.nextToken());
                return failInfo;
            } else {
                failInfo.add("Couldn't recieve fail message");
                return failInfo;
            }
        } catch (NamingException ex) {//andre problemer
            LOG.log(Level.SEVERE, null, ex);
            List<String> errMessage = new ArrayList<String>(1);
            errMessage.add(ex.toString());
            return errMessage;
        }
    }

    /**
     * <p>Deloppgave 2, get fortune cookie
     * <p>Kobler seg opp mot fullerdata web service og returnerer fortune
     * cookie.
     *
     * @param _username brukernavn som skal brukes ved beregning av aktuell
     * cookie
     * @return fortune cookie basert på angitt brukernavn og dagens dato
     */
    @WebMethod(operationName = "getFortuneCookies")
    public List<String> getFortuneCookies(@WebParam(name = "username") String _username) {
        String username = _username;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd"); //cookie må være avhengig av dato
        Date date = new Date();
        String dayToday = dateFormat.format(date);
        int maxCookie = readNodeCount(); //leser cookieantall
        int cookieNumber = computeCookieNumber(username, dayToday, maxCookie);//beregner cookienummer
        List<String> result = new ArrayList<String>(2);
        String cookie = getSpecificCookie(cookieNumber);
        result.add(cookie);
        result.add(getImage(cookie));
        return result;
    }

    /**
     * <p>Leser antall av noder fra cookielist på fullerdata.com
     *
     * @return antal noder
     */
    private int readNodeCount() {
        no.hin.stg.intapp.oblig3.del2.FullerDataX0020FortuneX0020CookieSoap port = service.getFullerDataX0020FortuneX0020CookieSoap();
        return port.readNodeCount();
    }

    /**
     * <p>Beregner dagens cookie basert på navn, dato og antall av cooikes
     *
     * @param username brukernavn som brukes for å beregne aktuelt svar
     * @param dayToday dagens dato
     * @param maxCookie mav antall av cookies
     * @return id for aktuell cookie
     */
    private int computeCookieNumber(String username, String dayToday, int maxCookie) {
        int result = Math.abs(username.hashCode() ^ dayToday.hashCode());
        while (result >= maxCookie) {
            result %= maxCookie;
        }
        assert result < maxCookie && result >= 0;
        return result;
    }

    /**
     * <p>Returnerer tekst for fortune cookie med aktuelt id
     *
     * @param index id for cookietekst som skal returneres
     * @return fortune cookie tekst
     */
    private String getSpecificCookie(int index) {
        no.hin.stg.intapp.oblig3.del2.FullerDataX0020FortuneX0020CookieSoap port = service.getFullerDataX0020FortuneX0020CookieSoap();
        return port.getSpecificCookie(index);
    }

    /**
     * <p>Laster ned bilde fra qrserver.com, generert fra angitt String
     * <code>cookie</code> konverterer deretter til
     * <code>Base64</code>
     *
     * @param cookie settning som skal konverteres til qr bilde
     * @return <code>Base64</code>representasjon av generert QR bilde
     */
    private String getImage(String cookie) {
        String _url;
        String result = null;
        try {
            _url = "http://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + URLEncoder.encode(cookie, "UTF-8");
            URL url = new URL(_url);
            BufferedImage image = ImageIO.read(url);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", out);
            byte[] bytes = out.toByteArray();
            result = Base64.encode(bytes);
        } catch (UnsupportedEncodingException ex) {
            LOG.log(Level.SEVERE, "UTF-8 is not supported? hmmm...");
        } catch (MalformedURLException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return result == null ? "" : result;
    }

    /**
     * <p>Deloppgave 3, beregne objektstørrelse
     *
     * @param _object objekt som størrelse skal beregnes til
     * @return bytelengde av angitt objekt
     * @throws IOException if any IO problem occurs
     */
    @WebMethod(operationName = "objectCounter")
    public Integer countByteLength(@WebParam(name = "_object") Object _object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(_object);
            byte[] bytes = bos.toByteArray();
            return bytes.length;
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex.getMessage());
        } finally {
            if (out != null) {
                out.close();
            }
            bos.close();
        }
        return -1;

    }
}
