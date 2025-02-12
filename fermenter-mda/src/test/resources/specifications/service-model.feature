@service
Feature: Specify services for use in model-driven file generation

    Scenario Outline: specify named services via a JSON metamodel
        Given a service named "<name>" in "<package>"
        When services are read
        Then a service metamodel instance is returned for the name "<name>" in "<package>"

        Examples:
            | name            | package         |
            | SomeAction      | my.action       |
            | SomeOtherAction | my.action.other |

    Scenario Outline: Namespace is used to incorrectly find services
        Given a service named "<name>" in "<package>"
        When services are read
        Then NO service metamodel instance is returned for the name "<name>" in "<lookupPackage>"

        Examples:
            | name            | package         | lookupPackage    |
            | SomeAction      | my.action       | alt.action       |
            | SomeOtherAction | my.action.other | alt.other.action |

    Scenario Outline: Use a no parameter operation in a service metamodel
        Given a service named "<name>" in "<package>" with an operation "<operationName>" with no parameters
        When services are read
        Then an operation "<operationName>" without parameters is found on service "<name>" in "<package>"

        Examples:
            | name            | package         | operationName |
            | SomeAction      | my.action       | operationA    |
            | SomeOtherAction | my.action.other | operationB    |

    Scenario Outline: Use a void return type operation in a service metamodel
        Given a service named "<name>" in "<package>" with an operation "<operationName>" with a void return type
        When services are read
        Then an operation "<operationName>" with a void return type is found on service "<name>" in "<package>"

        Examples:
            | name            | package         | operationName |
            | SomeAction      | my.action       | operationA    |
            | SomeOtherAction | my.action.other | operationB    |

    Scenario Outline: Use a base type as an operation parameter in a service metamodel
        Given a service named "<name>" in "<package>" with an operation "<operationName>" with parameters "<parameters>" of type "<parameterTypes>"
        When services are read
        Then an operation "<operationName>" is found on service "<name>" in "<package>" with parameters "<parameters>" of type "<parameterTypes>"

        Examples:
            | name            | package            | operationName  | parameters | parameterTypes        |
            | SomeAction      | my.action          | operationA     | p1         | string                |
            | SomeOtherAction | my.action.other    | operationB     | p2         | boolean               |
            | SomeAction2     | my.action.other    | operationB     | p3         | date                  |
            | MultiParam1     | my.action.other    | operationC     | p4, p5     | date, string          |
            | MultiParam2     | my.action.other    | operationD     | p6, p7, p8 | boolean, date, string |
            | DocumentUpload  | my.action.document | uploadDocument | p1         | document              |

    Scenario Outline: Use a base type as an operation return type in a service metamodel
        Given a service named "<name>" in "<package>" with an operation "<operationName>" with the return type "<returnType>"
        When services are read
        Then an operation "<operationName>" is found on service "<name>" in "<package>" with the return type "<returnType>"

        Examples:
            | name            | package         | operationName | returnType  |
            | SomeAction      | my.action       | operationA    | big_decimal |
            | SomeOtherAction | my.action.other | operationB    | timestamp   |
            | SomeAction2     | my.action.other | operationB    | integer     |

    Scenario Outline: Use an enumeration as an operation parameter in a service metamodel
        Given an enumeration named "<enumerationName>" in "<package>" and enum constants "<constants>"
        And a service named "<name>" in "<package>" with an operation "<operationName>" with parameters "<parameters>" of type "<parameterTypes>"
        When services are read
        Then an operation "<operationName>" is found on service "<name>" in "<package>" with parameters "<parameters>" of type "<parameterTypes>"

        Examples:
            | name            | package         | operationName | parameters | parameterTypes | enumerationName | constants |
            | SomeAction      | my.action       | operationA    | p1         | E1             | E1              | a,b       |
            | SomeOtherAction | my.action.other | operationB    | p2         | E2             | E2              | c,d       |
            | SomeAction2     | my.action.other | operationB    | p3         | E3             | E3              | e,f       |

    Scenario Outline: Use an enumeration as an operation return type in a service metamodel
        Given an enumeration named "<enumerationName>" in "<package>" and enum constants "<constants>"
        And a service named "<name>" in "<package>" with an operation "<operationName>" with the return type "<returnType>"
        When services are read
        Then an operation "<operationName>" is found on service "<name>" in "<package>" with the return type "<returnType>"

        Examples:
            | name            | package         | operationName | returnType | enumerationName | constants |
            | SomeAction      | my.action       | operationA    | E10        | E10             | a,b       |
            | SomeOtherAction | my.action.other | operationB    | E20        | E20             | c,d       |
            | SomeAction2     | my.action.other | operationB    | E30        | E30             | e,f       |

    Scenario Outline: Use a many parameter as an operation return type in a service metamodel
        Given a service named "<name>" in "<package>" with an operation "<operationName>" with many parameters "<parameters>" of type "<parameterTypes>"
        When services are read
        Then an operation "<operationName>" is found on service "<name>" in "<package>" with many parameters "<parameters>" of type "<parameterTypes>"

        Examples:
            | name            | package         | operationName | parameters | parameterTypes |
            | SomeAction      | my.action       | operationA    | p1         | string         |
            | SomeOtherAction | my.action.other | operationB    | p2         | boolean        |
            | SomeAction2     | my.action.other | operationB    | p3         | date           |

    Scenario Outline: Use a many return type as an operation return type in a service metamodel
        Given a service named "<name>" in "<package>" with an operation "<operationName>" with the many return type "<returnType>"
        When services are read
        Then an operation "<operationName>" is found on service "<name>" in "<package>" with the many return type "<returnType>"

        Examples:
            | name            | package         | operationName | returnType  |
            | SomeAction      | my.action       | operationA    | big_decimal |
            | SomeOtherAction | my.action.other | operationB    | timestamp   |
            | SomeAction2     | my.action.other | operationB    | integer     |

    Scenario Outline: Transaction types are set and retrieved appropriately
        Given a service named "<name>" in "<package>" with an operation "<operationName>" with the transaction attribute "<txAttribute>"
        When services are read
        Then an operation "<operationName>" is found on service "<name>" in "<package>" with the transaction attribute "<txAttribute>"

        Examples:
            | name           | package | operationName | txAttribute  |
            | RequiredTx     | my.tx   | operationA    | Required     |
            | SupportsTx     | my.tx   | operationB    | Supports     |
            | NotSupportedTx | my.tx   | operationC    | NotSupported |
            | MandatoryTx    | my.tx   | operationD    | Mandatory    |
            | RequiresNewTx  | my.tx   | operationE    | RequiresNew  |
            | NeverTx        | my.tx   | operationF    | Never        |

    Scenario Outline: Transaction types are defaulted appropriately
        Given a service named "<name>" in "<package>" with an operation "<operationName>" with a void return type in default
        When services are read
        Then an operation "<operationName>" is found on service "<name>" in "<package>" with the transaction attribute "<expectedTxAttribute>"

        Examples:
            | name           | package | operationName | expectedTxAttribute |
            | RequiredTx     | my.tx   | findByX       | Supports            |
            | SupportsTx     | my.tx   | queryByY      | Supports            |
            | NotSupportedTx | my.tx   | loadABC       | Supports            |
            | MandatoryTx    | my.tx   | operationD    | Required            |
            | RequiresNewTx  | my.tx   | operationE    | Required            |

    Scenario: Error returned when service name is not specified
        Given a service named "" in "error.missing.service.name"
        When services are read
        Then the tracker reports that errors were encountered

    Scenario: Error returned when an operation parameter name is not specified
        Given a service named "ServiceX" in "error.missing.param.info" with an operation "noParamType" with parameters "foo" of type ""
        When services are read
        Then the tracker reports that errors were encountered

    Scenario: Error returned when an operation parameter type is not specified
        Given a service named "ServiceX" in "error.missing.param.info" with an operation "noParamName" with parameters "" of type "string"
        When services are read
        Then the tracker reports that errors were encountered

    Scenario: Default return type to void when not specified
        Given a service named "ServiceX" in "return.type" with an operation "defaultReturnType" with the return type ""
        When services are read
        Then an operation "defaultReturnType" is found on service "ServiceX" in "return.type" with the return type "void"

    Scenario Outline: Specify that the response is a paged response
        Given a service with an operation named "<operationName>" with paged response "<pagedResponse>"
        When services are read
        Then a service metamodel instance is returned with operation "<operationName>" with paged response "<pagedResponse>"

        Examples:
            | operationName   | pagedResponse |
            | SomeAction      | enabled       |
            | SomeOtherAction | disabled      |

    Scenario: Throw an error if the service is noted as paged and there is no response type
        Given a service with an operation named "badOperation" with pagedResponse enabled and return type is void
        When services are read
        Then an error is thrown for "badOperation" because you cannot have pagedResponse enabled and return type is void

    Scenario Outline: Page Index and Page Size are automatically added as default parameters to paged responses
        Given a service with with a paged operation with "<parameters>" parameters
        When services are read
        Then the operation has "<parametersWithStartPageAndSize>" parameters which now included as the last two parameters

        Examples:
            | parameters                    | parametersWithStartPageAndSize                  |
            |                               | startPage, count                                |
            | oneParameter                  | oneParameter, startPage, count                  |
            | oneParameter, secondParameter | oneParameter, secondParameter, startPage, count |
            | thisIsABadIdea, startPage     | thisIsABadIdea, startPage, startPage, count     |

    Scenario: FUTURE: Use an entity as an operation parameter in a serivce metamodel

    Scenario: FUTURE: Use an entity as an operation return type in a serivce metamodel

    Scenario: FUTURE: Use an entity with many in a serivce metamodel
