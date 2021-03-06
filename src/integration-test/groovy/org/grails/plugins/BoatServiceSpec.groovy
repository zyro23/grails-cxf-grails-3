package org.grails.plugins

import geb.spock.GebSpec
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import wslite.soap.SOAPClient
import wslite.soap.SOAPFaultException
import wslite.soap.SOAPResponse
import wslite.soap.SOAPVersion

@Integration
@Rollback
class BoatServiceSpec extends GebSpec {

    SOAPClient client = new SOAPClient("http://localhost:${System.getProperty("server.port", "8080")}/services/boat")

    def "invoke the exposed method on the boat service"() {
        when:
        SOAPResponse response = client.send {
            envelopeAttributes "xmlns:test": 'http://test.cxf.grails.org/'
            body {
                'test:waterski' {}
            }
        }
        def methodResponse = response.body.waterskiResponse.return

        then:
        200 == response.httpResponse.statusCode
        SOAPVersion.V1_1 == response.soapVersion
        'SKI' == methodResponse.text()
    }

    def "invoke the hidden method on the boat service"() {
        when:
        client.send {
            envelopeAttributes "xmlns:test": 'http://test.cxf.grails.org/'
            body {
                'test:fish' {}
            }
        }

        then:
        thrown(SOAPFaultException)
    }

    def "invoke v1.1 service with soap v1.2 should fail"() {
        when:
        client.send {
            envelopeAttributes "xmlns:test": 'http://test.cxf.grails.org/'
            version SOAPVersion.V1_2
            body {
                'test:waterski' {}
            }
        }

        then:
        thrown(SOAPFaultException)
    }
}
