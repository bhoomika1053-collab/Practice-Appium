# Practice Appium

Starter Appium Java + TestNG project.

## Setup

1. Start Appium server on `http://127.0.0.1:4723`.
2. Update `src/test/resources/config.properties` with your device and app path.
3. Run tests:

```bash
mvn test
```

## Structure

- `src/test/java/base` Base test setup and teardown
- `src/test/java/tests` Test classes
- `src/test/java/utils` Utility classes
- `src/test/resources` Runtime config
