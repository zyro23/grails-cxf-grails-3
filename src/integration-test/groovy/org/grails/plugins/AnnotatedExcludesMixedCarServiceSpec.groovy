package org.grails.plugins

import geb.spock.GebSpec
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import wslite.soap.SOAPClient
import wslite.soap.SOAPResponse
import wslite.soap.SOAPVersion
import wslite.soap.SOAPFaultException

@Integration
@Rollback
class AnnotatedExcludesMixedCarServiceSpec extends GebSpec {

    SOAPClient client = new SOAPClient("http://localhost:${System.getProperty("server.port", "8080")}/services/annotatedExcludesMixedCar")

    def "honk the horn"() {
        when:
        SOAPResponse response = client.send {
            envelopeAttributes "xmlns:test": 'http://test.cxf.grails.org/'
            body {
                'test:honkHorn' {}
            }
        }
        def methodResponse = response.body.honkHornResponse

        then:
        200 == response.httpResponse.statusCode
        SOAPVersion.V1_1 == response.soapVersion
        'HONK' == methodResponse.toString()
    }

    def "dont honk the horn"() {
        when:
        SOAPResponse response = client.send {
            envelopeAttributes "xmlns:test": 'http://test.cxf.grails.org/'
            body {
                'test:dontHonk' {}
            }
        }

        then:
        thrown(SOAPFaultException)
    }

    def "start the car"() {
        when:
        SOAPResponse response = client.send {
            envelopeAttributes "xmlns:test": 'http://test.cxf.grails.org/'
            body {
                'test:start' {}
            }
        }
        def methodResponse = response.body.startResponse

        then:
        200 == response.httpResponse.statusCode
        SOAPVersion.V1_1 == response.soapVersion
        'GAS' == methodResponse.toString()
    }

    def "stop the car"() {
        when:
        SOAPResponse response = client.send {
            envelopeAttributes "xmlns:test": 'http://test.cxf.grails.org/'
            body {
                'test:stop' {}
            }
        }
        def methodResponse = response.body.stopResponse

        then:
        200 == response.httpResponse.statusCode
        SOAPVersion.V1_1 == response.soapVersion
        'BRAKES' == methodResponse.toString()
    }
}
