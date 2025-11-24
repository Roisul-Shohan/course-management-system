# Course Management System

A comprehensive web-based application for managing educational courses, students, and teachers. Built using Java Servlets, JSP, and MongoDB, it provides role-based access for administrators, teachers, and students to efficiently handle course assignments, registrations, and profiles.

## Live Demo

[View Live Demo](https://course-management-system-vdic.onrender.com/)

## Features

- **Admin Dashboard**: Manage courses, students, teachers, assign teachers to courses, view system statistics and activity logs.
- **Teacher Portal**: View assigned courses, manage enrolled students, update personal profile.
- **Student Portal**: Browse available courses, register for courses, view enrolled courses, update profile.
- **Authentication & Security**: User registration and login with JWT-based session management and password hashing.
- **Course Management**: Add, edit, delete courses; assign teachers; track student enrollments.
- **Profile Management**: Users can update their personal information.
- **Responsive UI**: Web-based interface with JavaScript for dynamic interactions.

## Technology Stack

- **Backend**: Java 11, Java Servlets, JSP
- **Database**: MongoDB
- **Build Tool**: Maven
- **Server**: Apache Tomcat 9
- **Security**: JWT (JSON Web Tokens), jBCrypt for password hashing
- **JSON Processing**: Jackson
- **Environment Management**: java-dotenv
- **Containerization**: Docker
- **Testing**: JUnit

## Prerequisites

- Java Development Kit (JDK) 11 or higher
- Apache Maven 3.8.4 or higher
- MongoDB (local installation or cloud instance like MongoDB Atlas)
- Docker (optional, for containerized deployment)

## Installation

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-username/CourseManagementSystem.git
   cd CourseManagementSystem
   ```

2. **Set Up Environment Variables**:
   - Copy `.env.example` to `.env`
   - Update `.env` with your MongoDB connection URI:
     ```
     MONGODB_URI=mongodb://localhost:27017
     ```
     Or use a cloud MongoDB URI if applicable.

3. **Build the Project**:
   ```bash
   mvn clean install
   ```

4. **Deploy Locally**:
   - Using Maven Tomcat Plugin:
     ```bash
     mvn tomcat7:run
     ```
   - Access the application at `http://localhost:8080`

## Usage

1. **Start the Application**:
    - If using Docker, build and run the container (see Deployment section).
    - Otherwise, deploy to Tomcat as described in Installation.

2. **Access the Application**:
    - Open a web browser and navigate to `http://localhost:8080` (or the deployed URL).

3. **User Roles**:
    - **Admin**: Sign up or log in with admin credentials to access the admin dashboard for managing the system.
    - **Teacher**: Log in to view and manage assigned courses and students.
    - **Student**: Log in to browse and register for available courses.

4. **Admin Login**:
    - To log in as an admin, use the username "admin" and password "aaa".
    - This provides access to the admin dashboard for full system management.

5. **Key Actions**:
    - Admins can add courses, assign teachers, and manage users.
    - Teachers can view course details and enrolled students.
    - Students can register for courses and track their enrollments.

## Deployment

### Using Docker

1. **Build the Docker Image**:
   ```bash
   docker build -t course-management-system .
   ```

2. **Run the Container**:
   ```bash
   docker run -p 8080:8080 --env-file .env course-management-system
   ```

3. **Access the Application**:
   - The application will be available at `http://localhost:8080`

### Manual Deployment to Tomcat

1. **Build the WAR File**:
   ```bash
   mvn clean package
   ```

2. **Deploy to Tomcat**:
   - Copy `target/CourseManagementSystem.war` to Tomcat's `webapps` directory.
   - Start Tomcat server.
   - Access at `http://localhost:8080/CourseManagementSystem`

For production deployment, consider using cloud platforms like AWS, Heroku, or Render for hosting Tomcat and MongoDB.

## Contributing

We welcome contributions to improve the Course Management System!

1. Fork the repository.
2. Create a new branch for your feature or bug fix: `git checkout -b feature/your-feature-name`
3. Make your changes and commit them: `git commit -m 'Add some feature'`
4. Push to the branch: `git push origin feature/your-feature-name`
5. Open a Pull Request.

Please ensure your code follows the project's coding standards and includes appropriate tests.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.