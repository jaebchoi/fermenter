@profile
Feature: Test profiles configuration

  Scenario Outline: Target(s) is contained in the profiles.json
    Given profiles are loaded from "<profilePath>" and targets are loaded from "<targetPath>"
    When profiles and targets are read
    Then a valid profile "<expectedProfileName>" is available and target references "<targetReferences>" are contained
      Examples:
          | profilePath                                      | targetPath                                      | expectedProfileName   | targetReferences               |
          | src/test/resources/example/example-profiles.json | src/test/resources/example/example-targets.json | example-only-targets  | exampleTarget1, exampleTarget2 |

  Scenario Outline: Target is not contained in the profiles.json
    Given profiles are loaded from "<profilePath>" and targets are loaded from "<targetPath>"
    When profiles and targets are read
    Then a valid profile "<expectedProfileName>" is available and target reference is empty
      Examples:
          | profilePath                                      | targetPath                                      | expectedProfileName   |
          | src/test/resources/example/example-profiles.json | src/test/resources/example/example-targets.json | example-only-profiles |
          | src/test/resources/example/example-profiles.json | src/test/resources/example/example-targets.json | example-deprecated    |


  Scenario Outline: Other Profile(s) composited in the profiles.json
    Given profiles are loaded from "<profilePath>" and targets are loaded from "<targetPath>"
    When profiles and targets are read
    Then a valid profile "<expectedProfileName>" is available and other profile references "<profileReferences>" are contained
      Examples:
          | profilePath                                        | targetPath                                     | expectedProfileName   | profileReferences  |
          | src/test/resources/example/example-profiles.json   | src/test/resources/example/example-targets.json | example-only-profiles | example-deprecated |

  Scenario Outline: Other Profile(s) not composited in the profiles.json
    Given profiles are loaded from "<profilePath>" and targets are loaded from "<targetPath>"
    When profiles and targets are read
    Then a valid profile "<expectedProfileName>" is available and other profile reference is empty
      Examples:
          | profilePath                                      | targetPath                                      | expectedProfileName   |
          | src/test/resources/example/example-profiles.json | src/test/resources/example/example-targets.json | example-only-targets  |
          | src/test/resources/example/example-profiles.json | src/test/resources/example/example-targets.json | example-deprecated    |

  Scenario Outline: Profile(s) is not deprecated in the profiles.json
    Given profiles are loaded from "<profilePath>" and targets are loaded from "<targetPath>"
    When profiles and targets are read
    Then a valid profile "<expectedProfileName>" is available and profile is not deprecated
      Examples:
          | profilePath                                      | targetPath                                      | expectedProfileName   |
          | src/test/resources/example/example-profiles.json | src/test/resources/example/example-targets.json | example-only-targets  |
          | src/test/resources/example/example-profiles.json | src/test/resources/example/example-targets.json | example-only-profiles |


  Scenario Outline: Profile(s) is deprecated in the profiles.json
    Given profiles are loaded from "<profilePath>" and targets are loaded from "<targetPath>"
    When profiles and targets are read
    Then a valid profile "<expectedProfileName>" is available and profile is deprecated
      Examples:
          | profilePath                                      | targetPath                                      | expectedProfileName   |
          | src/test/resources/example/example-profiles.json | src/test/resources/example/example-targets.json | example-deprecated    |
