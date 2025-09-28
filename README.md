# Customer Management System

A comprehensive customer information management system built with Spring Boot, featuring a modern web interface for managing customer data with full CRUD operations.

## Features

- **Customer Management**: Complete CRUD operations (Create, Read, Update, Delete)
- **Advanced Search**: Filter customers by name, phone, email with pagination
- **Responsive Design**: Mobile-friendly interface using Bootstrap 5
- **Data Validation**: Server-side and client-side form validation
- **Database Support**: H2 (development) and MySQL (production)
- **REST API**: RESTful endpoints for programmatic access
- **Professional UI**: Clean, modern interface with intuitive navigation

## Technology Stack

- **Backend**: Spring Boot 2.7.18
- **Web Framework**: Spring MVC
- **Data Layer**: Spring Data JPA with Hibernate
- **Template Engine**: Thymeleaf with Layout Dialect
- **Database**: H2 (dev) / MySQL 8.0+ (prod)
- **Frontend**: Bootstrap 5, Font Awesome, Custom CSS/JavaScript
- **Build Tool**: Maven
- **Java Version**: 8+

## Prerequisites

- Java 8 or higher
- Maven 3.6+
- MySQL 8.0+ (for production)
- IDE (IntelliJ IDEA, Eclipse, VS Code)

## Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd SpringMVC
```

### 2. Development Environment (H2 Database)
```bash
# Run with default profile (H2 in-memory database)
mvn spring-boot:run

# Access the application
open http://localhost:8080
```

### 3. Production Environment (MySQL)

#### Setup MySQL Database
```sql
-- Connect to MySQL
mysql -u root -p

-- Create database
CREATE DATABASE customer_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user (optional)
CREATE USER 'customer_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON customer_management.* TO 'customer_user'@'localhost';
FLUSH PRIVILEGES;
```

#### Configure Application
Update `src/main/resources/application-prod.properties`:
```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
```

#### Run with Production Profile
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## Project Structure

```
src/
├── main/
│   ├── java/com/example/customer/
│   │   ├── CustomerManagementApplication.java    # Main application class
│   │   ├── controller/
│   │   │   ├── CustomerController.java           # Web & REST controllers
│   │   │   └── HomeController.java               # Home redirect controller
│   │   ├── entity/
│   │   │   └── Customer.java                     # Customer entity
│   │   ├── repository/
│   │   │   └── CustomerRepository.java           # Data access layer
│   │   └── service/
│   │       ├── CustomerService.java              # Service interface
│   │       └── impl/CustomerServiceImpl.java     # Service implementation
│   └── resources/
│       ├── application.properties                # Development configuration
│       ├── application-prod.properties           # Production configuration
│       ├── schema.sql                           # Database schema
│       ├── data.sql                             # Initial data
│       ├── static/
│       │   ├── css/style.css                    # Custom styles
│       │   └── js/app.js                        # Custom JavaScript
│       └── templates/
│           ├── customers/
│           │   ├── list.html                    # Customer list page
│           │   ├── form.html                    # Add/Edit form
│           │   └── detail.html                  # Customer details
│           └── fragments/
│               └── layout.html                  # Base layout template
```

## API Endpoints

### Web Interface
- `GET /` - Redirect to customer list
- `GET /customers` - Customer list with search and pagination
- `GET /customers/new` - Add new customer form
- `POST /customers` - Create new customer
- `GET /customers/{id}` - View customer details
- `GET /customers/{id}/edit` - Edit customer form
- `POST /customers/{id}` - Update customer
- `POST /customers/{id}/delete` - Delete customer

### REST API
- `GET /api/customers` - Get all customers (paginated)
- `POST /api/customers` - Create new customer
- `GET /api/customers/{id}` - Get customer by ID
- `PUT /api/customers/{id}` - Update customer
- `DELETE /api/customers/{id}` - Delete customer

## Database Schema

### Customer Table
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| name | VARCHAR(50) | NOT NULL | Customer name |
| phone | VARCHAR(11) | NOT NULL, UNIQUE | Phone number |
| email | VARCHAR(100) | - | Email address |
| address | VARCHAR(200) | - | Physical address |
| age | INT | - | Age |
| gender | VARCHAR(10) | - | Gender (MALE/FEMALE/OTHER) |
| created_time | DATETIME | NOT NULL | Creation timestamp |
| updated_time | DATETIME | - | Last update timestamp |

## Configuration

### Development (H2)
```properties
# H2 in-memory database
spring.datasource.url=jdbc:h2:mem:customerdb
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=create-drop
```

### Production (MySQL)
```properties
# MySQL database
spring.datasource.url=jdbc:mysql://localhost:3306/customer_management
spring.jpa.hibernate.ddl-auto=none
spring.sql.init.mode=always
```

## Features Overview

### Customer Management
- Add new customers with validation
- View customer details
- Edit customer information
- Delete customers with confirmation
- Search and filter customers
- Pagination support

### Data Validation
- Name: 2-50 characters, required
- Phone: 11-digit format, unique, required
- Email: Valid email format
- Age: 0-150 range
- Gender: Male/Female/Other options

### User Interface
- Responsive Bootstrap 5 design
- Font Awesome icons
- Interactive tables with sorting
- Modal confirmations
- Toast notifications
- Form validation feedback

## Deployment

### JAR Deployment
```bash
# Build the application
mvn clean package

# Run the JAR file
java -jar target/SpringMVC-1.0-SNAPSHOT.jar --spring.profiles.active=prod
```

### Docker Deployment
```dockerfile
FROM openjdk:8-jre-slim
COPY target/SpringMVC-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=prod"]
```

## Testing

```bash
# Run tests
mvn test

# Run with coverage
mvn test jacoco:report
```

## Development Notes

### Adding New Features
1. Create entity classes in `entity` package
2. Add repository interfaces extending `JpaRepository`
3. Implement service layer with business logic
4. Create controllers for web/API endpoints
5. Design Thymeleaf templates for UI

### Database Migration
For production deployments, consider using Flyway:
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

## Troubleshooting

### Common Issues

**Database Connection Failed**
- Verify MySQL is running
- Check connection credentials
- Ensure database exists

**Data Not Loading**
- Check `schema.sql` and `data.sql` syntax
- Verify `spring.sql.init.mode=always`
- Review application logs for errors

**Template Not Found**
- Ensure Thymeleaf Layout Dialect dependency
- Check template file locations
- Verify controller return values

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Check the documentation

## Version History

- **v1.0.0** - Initial release with core CRUD functionality
- Customer management with full web interface
- REST API support
- Multiple database environment support