package server

import graphql.GraphQL
import graphql.schema.StaticDataFetcher
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.RuntimeWiring.newRuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeRuntimeWiring
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Application

fun main(args: Array<String>) {
//    runApplication<Application>(*args)
    val schema: String = """
        type Query {
            hello: String
            food: String
            human: Human
        }
        type Human {
            name: String
            age: Int!
        }
        """.trimIndent()

    val schemaParser = SchemaParser()
    val typeRegistry = schemaParser.parse(schema)

    val runtimeWiring =
            newRuntimeWiring()
                    .type("Query") {
                        builder -> builder
                            .dataFetcher("hello",StaticDataFetcher("Hello World"))
                            .dataFetcher("food",StaticDataFetcher("I like apple"))
                    }
                    .type("Human") {
                        builder -> builder
                            .dataFetcher("name",StaticDataFetcher("李白"))
                            .dataFetcher("age",StaticDataFetcher(18))
                    }
                    .build()

    val schemaGenerator = SchemaGenerator()
    val graphqlSchema = schemaGenerator.makeExecutableSchema(typeRegistry,runtimeWiring)
    val graphql = GraphQL.newGraphQL(graphqlSchema).build()

    val result = graphql.execute("{hello,food}")

    println(result)

}