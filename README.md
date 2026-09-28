# Library Management System
This project is a console-based Library Management System written in Java for the ICS4UE Java Review Assignment.
## Features
- Load library items and users from text files when the program starts
- Search items by ID, title, creator, genre, or rating
- Check out and check in items
- Create reservations for unavailable items
- Send items for repair and return repaired items to the inventory
- Add new items with MasterLibrarian approval
## Run the program
Open PowerShell in this folder and run:
```powershell
javac -encoding UTF-8 *.java
java Main
```
The program expects `inventory.txt` and `users.txt` to be in the same folder.
## Sample user IDs
| ID | Role |
|---|---|
| C001 | Customer |
| C002 | Customer |
| E001 | Employee |
| M001 | MasterLibrarian |
## Design notes
- `LibraryItem` and `User` are abstract classes.
- `Book`, `Magazine`, and `Movie` inherit from `LibraryItem`.
- `Customer`, `Employee`, and `MasterLibrarian` inherit from `User`.
- `Library` contains the item, user, loan, and reservation arrays.
- `Loan` and `Reservation` connect items with users.
- `getDetails()` demonstrates Polymorphism because each item type formats its own details.
- The program uses fixed-size arrays and batch file loading to match the assignment requirements.
