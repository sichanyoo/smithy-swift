package serde.wrappedxml

import TestContext
import defaultSettings
import getFileContents
import io.kotest.matchers.string.shouldContainOnlyOnce
import mocks.MockHttpAWSQueryProtocolGenerator
import org.junit.jupiter.api.Test

class StructDecodeWrappedXMLGeneratorTests {

    @Test
    fun `wrapped map decodable`() {
        val context = setupTests("Isolated/wrappedxml/flattened-map.smithy", "aws.protocoltests.query#AwsQuery")
        val contents = getFileContents(context.manifest, "/Example/models/FlattenedXmlMapOutputBody+Decodable.swift")
        val expectedContents = """
        extension FlattenedXmlMapOutputBody: Decodable {
            enum CodingKeys: String, CodingKey {
                case myMap
            }
        
            public init (from decoder: Decoder) throws {
                let topLevelContainer = try decoder.container(keyedBy: Key.self)
                let containerValues = try topLevelContainer.nestedContainer(keyedBy: CodingKeys.self, forKey: Key("FlattenedXmlMapResult"))
                if containerValues.contains(.myMap) {
                    struct KeyVal0{struct key{}; struct value{}}
                    let myMapWrappedContainer = containerValues.nestedContainerNonThrowable(keyedBy: MapEntry<String, String, KeyVal0.key, KeyVal0.value>.CodingKeys.self, forKey: .myMap)
                    if myMapWrappedContainer != nil {
                        let myMapContainer = try containerValues.decodeIfPresent([MapKeyValue<String, String, KeyVal0.key, KeyVal0.value>].self, forKey: .myMap)
                        var myMapBuffer: [String:String]? = nil
                        if let myMapContainer = myMapContainer {
                            myMapBuffer = [String:String]()
                            for stringContainer0 in myMapContainer {
                                myMapBuffer?[stringContainer0.key] = stringContainer0.value
                            }
                        }
                        myMap = myMapBuffer
                    } else {
                        myMap = [:]
                    }
                } else {
                    myMap = nil
                }
            }
        }
        """.trimIndent()

        contents.shouldContainOnlyOnce(expectedContents)
    }
    private fun setupTests(smithyFile: String, serviceShapeId: String): TestContext {
        val context = TestContext.initContextFrom(smithyFile, serviceShapeId, MockHttpAWSQueryProtocolGenerator()) { model ->
            model.defaultSettings(serviceShapeId, "Example", "2020-01-08", "aws query protocol")
        }
        context.generator.generateDeserializers(context.generationCtx)
        context.generationCtx.delegator.flushWriters()
        return context
    }
}
