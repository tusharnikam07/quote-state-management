# quote-state-management
**Quote State Management**

**Overview**

This project manages the lifecycle and states of a quote object. It uses Thymeleaf templates to create HTML versions of quotes and turns them into PDFs when needed.

**Features**
1. Handles state transitions of quotes.
2. Creates HTML files for quotes using Thymeleaf.
3. Converts HTML to PDF.
4. Includes tests to check all transitions and cases.

**Usage**
**Run Tests**
A test class checks all state transitions and scenarios in the Quote class.

**Run tests with:**
mvn test

**Generate HTML**:
The quoteTemplate.html file in src/main/resources/templates defines how the HTML looks.
The generateHtml method in the Quote class fills the template with data.

**Generate PDF**
After creating the HTML, the project converts it to a PDF.
