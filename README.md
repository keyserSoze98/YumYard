YumYard - A Recipe Discovery Android App
📌 App Overview
Smart Daily Expense Tracker is a multi-screen Android app for small business owners to quickly log, view, and analyze daily expenses.
It features an intuitive Jetpack Compose UI, clean MVVM architecture, and mock analytics for better cash flow insights.
Currently, all data is stored in-memory — persistence with Room can be added later.

🤖 AI Usage Summary
This project was built entirely with an AI-first approach using ChatGPT.
AI assisted in:

Designing Jetpack Compose UI layouts and state management patterns.
Generating ViewModel, Repository, and data class scaffolding.
Refining UX through multiple prompt iterations (spacing, labels, animations).
Writing code comments, README content, and validation logic.
Suggesting architecture and navigation structure.
📜 Prompt Logs (Key Prompts + Retries)
Prompt 1: "Generate Jetpack Compose Expense Entry Screen with Title, Amount, Category dropdown, Notes, Receipt image picker, and Total Spent Today display, using MVVM with StateFlow."
Prompt 2: "Write a ViewModel for in-memory expense tracking with functions to add expense, calculate daily total, and group expenses by category."
Prompt 3: "Suggest a 3-screen navigation structure in Jetpack Compose with Expense Entry, Expense List, and Expense Report screens."
Prompt 4: "Generate mock data for last 7 days and show category-wise totals in a BarChart (mocked) in Compose."
Prompt 5: "Fix bar chart spacing, date formatting, and ensure labels display correctly."
(Further retries focused on bug fixes, padding tweaks, and animation improvements.)

✅ Checklist of Features Implemented
Feature	Status
Expense Entry Screen	✅
Title (text)	✅
Amount (₹)	✅
Category dropdown (Staff, Travel, Food, Utility)	✅
Optional notes (≤100 chars)	✅
Optional receipt image (mock)	✅
Submit button with Toast + animation	✅
Real-time “Total Spent Today” display	✅
Expense List Screen	✅
View Today’s expenses (default)	✅
Filter by date	✅
Group toggle: Category / Time	✅
Show total count, total amount	✅
Empty state handling	✅
Expense Report Screen	✅
Mock 7-day report with daily totals	✅
Category-wise totals	✅
Bar chart (mocked, fixed spacing & labels)	✅
Navigation between screens	✅
State Management (ViewModel + StateFlow)	✅
Validation (amount > 0, title non-empty)	✅
Light/Dark theme	✅
Room Persistence for Data Storage (optional bonus)	✅
Export PDF/CSV (mock)	❌
Share Intent	❌
📱 Screenshots
Entry Screen	List Screen	Report Screen
Entry	List	Report
📦 APK Download
Download APK
(Or find it in the /apk/ folder in this repo.)

🛠️ Tech Stack
Layer	Technology
Language	Kotlin
UI	Jetpack Compose
Architecture	MVVM
State Management	ViewModel + StateFlow
Data Layer	Room Database
Navigation	Jetpack Navigation Compose
🚀 How to Run
Clone the repository:

git clone https://github.com/keyserSoze98/SmartDailyExpenseTracker.git
Open in Android Studio.

Sync Gradle and run on an emulator or device.
