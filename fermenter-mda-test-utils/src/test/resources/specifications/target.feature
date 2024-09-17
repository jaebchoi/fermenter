@target
Feature: Test profiles configuration

  Scenario Outline: Target(s) is contained in the targets.json
    Given targets are loaded from "<targetPath>"
    When targets are read
    Then a valid target "<expectedTargetName>" is available
      Examples:
          | targetPath                                      | expectedTargetName |
          | src/test/resources/example/example-targets.json | exampleTarget1     |

  Scenario Outline: Target(s) is not contained in the targets.json
    Given targets are loaded from "<targetPath>"
    When targets are read
    Then an invalid target "<expectedTargetName>" is not available
      Examples:
          | targetPath                                      | expectedTargetName     |
          | src/test/resources/example/example-targets.json | invalidTarget          |


  Scenario Outline: Template file exists in the targets.json
    Given targets are loaded from "<targetPath>"
    When targets are read
    Then a valid target "<expectedTargetName>" is available and valid template file exists
      Examples:
          | targetPath                                      | expectedTargetName     |
          | src/test/resources/example/example-targets.json | exampleTarget1         |

  Scenario Outline: Template file does not exists in the targets.json
    Given targets are loaded from "<targetPath>"
    When targets are read
    Then a valid target "<expectedTargetName>" is available and invalid template file does not exist
      Examples:
          | targetPath                                      | expectedTargetName     |
          | src/test/resources/example/example-targets.json | exampleTarget2         |



  Scenario Outline: Generator File exists in the targets.json
    Given targets are loaded from "<targetPath>"
    When targets are read
    Then a valid target "<expectedTargetName>" is available and valid generator file exists
      Examples:
          | targetPath                                      | expectedTargetName     |
          | src/test/resources/example/example-targets.json | exampleTarget1         |

  Scenario Outline: Generator File does not exists in the targets.json
    Given targets are loaded from "<targetPath>"
    When targets are read
    Then a valid target "<expectedTargetName>" is available and invalid generator file does not exist
      Examples:
          | targetPath                                      | expectedTargetName     |
          | src/test/resources/example/example-targets.json | exampleTarget3         |
