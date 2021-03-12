package software.amazon.smithy.swift.codegen.integration

import software.amazon.smithy.model.shapes.Shape
import software.amazon.smithy.model.traits.EndpointTrait

class EndpointTraitConstructor(private val endpointTrait: EndpointTrait, private val inputShape: Shape) {
    fun construct(): String {
        return endpointTrait.hostPrefix.segments.joinToString(separator = "") { segment ->
            if (segment.isLabel) {
                // hostLabel can only target string shapes
                // see: https://awslabs.github.io/smithy/1.0/spec/core/endpoint-traits.html#hostlabel-trait
                val member = inputShape.members().first { it.memberName == segment.content }
                "\\(input.${member.memberName})"
            } else {
                segment.content
            }
        }
    }
}