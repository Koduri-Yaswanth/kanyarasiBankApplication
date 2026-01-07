# Kanyarasi Bank Frontend

Angular frontend application for Kanyarasi Bank management system.

## Features

- **User Registration**: Create new bank accounts
- **User Login**: Secure authentication
- **User Dashboard**: 
  - View account information
  - Make transactions (Deposit, Withdrawal, Transfer)
  - View transaction history
- **Admin Dashboard**:
  - View and manage all users
  - Approve/Disapprove pending account requests
  - View all transactions
  - Delete transactions and users

## Technology Stack

- Angular 21
- TypeScript
- RxJS
- Angular Router
- Angular Forms

## Development Server

Run `npm start` or `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory.

## Backend Connection

The frontend is configured to connect to the backend API running on `http://localhost:9990`.

Make sure the backend is running before using the frontend application.

## Color Theme

The application uses a professional white and blue color palette:
- Primary Blue: #2563eb
- White backgrounds
- Clean, minimal design
