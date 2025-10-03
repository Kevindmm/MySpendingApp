# MySpendingApp

**MySpendingApp** is a lightweight web application to track spending over time with support for multiple currencies.  
It provides a simple backend API built with **Spring Boot** and a frontend dashboard built with **React**, making it easy to visualize financial data in real-time.

## 🚀 Features

- Track and visualize spending trends over time.
- Filter results by day, month, or year.
- Multi-currency support (e.g., CAD, EUR, USD).
- Sort spending data by start date.
- Group spending automatically by date range.
- Easy integration with third-party APIs for currency exchange rates (planned).

## 📌 Roadmap – MySpendingApp (Java 17)

**✅ Already Done**
- **Basic Filters & Grouping**: Implemented grouping by day, month, and year using `LocalDate` in Java 17.
- **Multi-Currency Support (CAD, EUR, USD)**: Added dropdown and logic for different currencies.
- **Sorting by Start Date**: Spending data sorted with modern Java Streams and Comparator APIs.
- **GitHub Actions CI**: Set up workflows to build and test both backend and frontend automatically.

---

**🔄 In Progress**
- **GitHub Pages Deployment**: Configure automatic deployment of the React frontend to GitHub Pages after successful builds.
- **DTO Refactor with Records**: Gradually replacing old DTO classes with Java 17 `record` types for immutability and cleaner JSON mapping.

---

**📝 Planned**
- **Switch Expressions**: Refactoring legacy `switch` statements to use modern `switch ->` expressions, especially in filters and grouping logic.
- **Pattern Matching for instanceof**: Starting to simplify some casting code in validation and mapping layers.
- **Sealed Classes**: Define a sealed hierarchy for `Spending` types (Daily, Monthly, Yearly) to make aggregation logic more explicit and safe.
- **Text Blocks**: Adopt text blocks (`"""`) for longer SQL queries and seed data to improve readability.
- **Enhanced Random Generators**: Use `RandomGeneratorFactory` for generating seed data in testing scenarios.
- **Better Documentation**: Add technical notes in the README about which Java 17 features are used and why.


## Language & Tools

- [JDK17](https://jdk.java.net/archive/)
- [Gradle](https://gradle.org/) - as a package manager
- [Spring Boot](https://spring.io/projects/spring-boot) - as a server-side framework
- [React](https://reactjs.org/) - client-side framework

## Quickstart

This section contains all the information required for getting the server up and running. The application contains the following two directories:

- [server/](server/) - the server directory that contains the server code
- [client/](client/) - the client directory that contains the client code

In order to run the application, you will need to have both the server and client running in separate terminals.

### Server

To run the server, follow these steps:

1. Navigate to the server directory (in Unix that would be `cd server`)

2. Install the required dependencies by running `gradle build` or `gradlew build` if you're in Windows.

3. Start the server. `gradle bootRun` or `gradlew bootRun` - launches the Tomcat Server in debug mode on port 8080.

### Client

- System requirements
  - NodeJS v18

To run the client, follow these steps:

1. Navigate to the client directory (in Unix that would be `cd client`)

2. Install dependencies

```bash
npm install
```

You can ignore the severity vulnerabilities, this is a [known issue](https://github.com/facebook/create-react-app/issues/11174) related to `create-react-app` and are not real vulnerabilities.

3. Start the client

```bash
npm start
```

## Formatting Client

To format your code using [prettier](https://prettier.io/), follow these steps:

1. Navigate to the client directory (in Unix that would be `cd client`)

2. Run this command:

```bash
npm run lint
```

To ensure you are using the correct version of prettier, make sure to format your code after installing the dependencies (`npm install`).

## Database and Seed Data

**Note: No database setup should be required to get started with running the project.**

This project uses SQLite, which stores your tables inside a file (`server/database.db`) for development. You can inspect the seed data by looking into the (`server/src/main/resources`) folder in the main package.

The database is seeding from the `data.sql` file everytime the application is running. The data is seeded relative to today's date.

## Verify That Everything Is Set Up Correctly

To verify that the frontend is working properly, go to [http://localhost:3000](http://localhost:3000). You should see the homepage that is titled "Welcome to My Spending App" and a chart as below.

![Starting Screen](docs/mySpendingApp.png)
