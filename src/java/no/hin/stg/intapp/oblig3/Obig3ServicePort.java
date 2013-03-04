
package no.hin.stg.intapp.oblig3;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

/**
 * REST Web Service
 *
 * @author Timur Samkharadze timur.samkharadze@gmail.com
 */
@Path("obig3serviceport")
public class Obig3ServicePort {
    private no.hin.stg.intapp.oblig3_client.Obig3Service port;

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of Obig3ServicePort
     */
    public Obig3ServicePort() {
        port = getPort();
    }

    /**
     * Invokes the SOAP method autentificate
     * @param name resource URI parameter
     * @param password resource URI parameter
     * @return an instance of javax.xml.bind.JAXBElement<no.hin.stg.intapp.oblig3_client.AutentificateResponse>
     */
    @GET
    @Produces("application/xml")
    @Consumes("text/plain")
    @Path("autentificate/")
    public JAXBElement<no.hin.stg.intapp.oblig3_client.AutentificateResponse> getAutentificate(@QueryParam("name") String name, @QueryParam("password") String password) {
        try {
            // Call Web Service Operation
            if (port != null) {
                java.util.List<java.lang.String> result = port.autentificate(name, password);

                class AutentificateResponse_1 extends no.hin.stg.intapp.oblig3_client.AutentificateResponse {

                    AutentificateResponse_1(java.util.List<java.lang.String> _return) {
                        this._return = _return;
                    }
                }
                no.hin.stg.intapp.oblig3_client.AutentificateResponse response = new AutentificateResponse_1(result);
                return new no.hin.stg.intapp.oblig3_client.ObjectFactory().createAutentificateResponse(response);
            }
        } catch (Exception ex) {
            // TODO handle custom exceptions here
        }
        return null;
    }

    /**
     * Invokes the SOAP method getFortuneCookies
     * @param username resource URI parameter
     * @return an instance of javax.xml.bind.JAXBElement<no.hin.stg.intapp.oblig3_client.GetFortuneCookiesResponse>
     */
    @GET
    @Produces("application/xml")
    @Consumes("text/plain")
    @Path("getfortunecookies/")
    public JAXBElement<no.hin.stg.intapp.oblig3_client.GetFortuneCookiesResponse> getFortuneCookies(@QueryParam("username") String username) {
        try {
            // Call Web Service Operation
            if (port != null) {
                java.util.List<java.lang.String> result = port.getFortuneCookies(username);

                class GetFortuneCookiesResponse_1 extends no.hin.stg.intapp.oblig3_client.GetFortuneCookiesResponse {

                    GetFortuneCookiesResponse_1(java.util.List<java.lang.String> _return) {
                        this._return = _return;
                    }
                }
                no.hin.stg.intapp.oblig3_client.GetFortuneCookiesResponse response = new GetFortuneCookiesResponse_1(result);
                return new no.hin.stg.intapp.oblig3_client.ObjectFactory().createGetFortuneCookiesResponse(response);
            }
        } catch (Exception ex) {
            // TODO handle custom exceptions here
        }
        return null;
    }

    /**
     * Invokes the SOAP method objectCounter
     * @param object resource URI parameter
     * @return an instance of javax.xml.bind.JAXBElement<java.lang.Integer>
     */
    @POST
    @Produces("application/xml")
    @Consumes("application/xml")
    @Path("objectcounter/")
    public JAXBElement<Integer> postObjectCounter(JAXBElement<Object> object) {
        try {
            // Call Web Service Operation
            if (port != null) {
                java.lang.Integer result = port.objectCounter(object.getValue());
                return new JAXBElement<java.lang.Integer>(new QName("http//lang.java/", "integer"), java.lang.Integer.class, result);
            }
        } catch (Exception ex) {
            // TODO handle custom exceptions here
        }
        return null;
    }

    /**
     *
     */
    private no.hin.stg.intapp.oblig3_client.Obig3Service getPort() {
        try {
            // Call Web Service Operation
            no.hin.stg.intapp.oblig3_client.Oblig3Service service = new no.hin.stg.intapp.oblig3_client.Oblig3Service();
            no.hin.stg.intapp.oblig3_client.Obig3Service p = service.getObig3ServicePort();
            return p;
        } catch (Exception ex) {
            // TODO handle custom exceptions here
        }
        return null;
    }
}
