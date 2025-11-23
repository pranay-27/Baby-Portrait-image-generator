# Baby Portrait Generator AI

An AI-powered application that generates artistic baby portraits using Stability AI and Cloudinary.

## Features

- Upload baby photos
- Select from multiple artistic styles
- AI-powered image generation using Stability AI
- Cloud storage with Cloudinary
- Download generated portraits
- Modern web interface

## Prerequisites

- Java 21
- Maven 3.6+
- PostgreSQL database
- Cloudinary account
- Stability AI API key

## Setup

### 1. Environment Variables

Create a `.env` file in the root directory with the following variables:

```bash
# Cloudinary Configuration
CLOUDINARY_CLOUD_NAME=your_cloudinary_cloud_name
CLOUDINARY_API_KEY=your_cloudinary_api_key
CLOUDINARY_API_SECRET=your_cloudinary_api_secret

# Stability AI Configuration
STABILITY_API_KEY=your_stability_api_key
```

### 2. Database Setup

1. Create a PostgreSQL database named `babyimageDB`
2. Update database credentials in `src/main/resources/application.properties` if needed

### 3. Build and Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

## API Endpoints

- `GET /api/` - Home page
- `GET /api/create` - Portrait creation page
- `GET /api/styles` - Get available styles
- `POST /api/generate` - Generate portrait
- `GET /api/download/{id}` - Download generated portrait

## How It Works

1. User uploads a baby photo
2. User selects an artistic style
3. Application uploads original photo to Cloudinary
4. Stability AI generates styled portrait
5. Generated image is uploaded to Cloudinary
6. Portrait data is saved to database
7. User can download the generated portrait

## Technologies Used

- Spring Boot 3.5.5
- Spring Data JPA
- Thymeleaf
- PostgreSQL
- Cloudinary
- Stability AI API
- Tailwind CSS
- Axios
