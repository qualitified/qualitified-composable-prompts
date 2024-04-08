# qualitified-composable-prompts
A plugin that provides a simple and easy to use integration pattern between [Composable Prompts](https://composableprompts.com/) and the Nuxeo Platform. 


# How to build
```bash
git clone https://github.com/qualitified/qualitified-composable-prompts.git
cd qualitified-composable-prompts
mvn clean install -DskipTests
```

# Features
## Automation API
The integration between the Nuxeo Platform and ComposablePrompts is meant to be as versatile as possible and leverages Nuxeo's automation framework.

### Run Execution
The operation to execute a Composable Prompts interaction is `Qualitified.ComposablePrompts`

Parameters:

| Name             | Description                                        | Type            | Required | Default value |
|:-----------------|:---------------------------------------------------|:----------------|:---------|:--------------|
| interactionId    | The interaction ID                                 | string          | true     |               |
| data             | The interaction JSON input data                    | json object     | true     |               |
| model            | The Model name                                     | string          | true     |               |

Output: A string Blob containing the Composable Prompt REST API JSON response
