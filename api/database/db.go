package database

import (
	"database/sql"
	"fmt"

	_ "github.com/lib/pq"
)

type User struct {
	Name     string
	Password string
}

type Repository interface {
	CreateUser(user User) (bool, error)
	LoginUser(user User) (*User, error)
	GetUserImages(userName string) ([]string, error)
	SaveNewImage(imagePath string) (bool, error)
}

type Database struct {
	db *sql.DB
}

func (db Database) CreateUser(user User) (bool, error) {
	_, err := db.db.Query("INSERT INTO users (id, name) VALUES (4, 'Rafael')")

	if err != nil {
		fmt.Print("fail to select")
		fmt.Print(err.Error())
		return false, err
	}

	return true, nil
}

func (dbInstance Database) GetUserImages(userName string) ([]string, error) {
	var row string
	var userFilesPaths []string

	rows, err := dbInstance.db.Query("SELECT * FROM users")

	if err != nil {
		fmt.Print("fail to select")
		return nil, err
	}
	defer rows.Close()

	for rows.Next() {
		rows.Scan(nil, &row)
		userFilesPaths = append(userFilesPaths, row)
		fmt.Println(row)
	}

	return userFilesPaths, nil
}

func GetInstance() Database {
	connStr := "postgresql://postgres:ganapppass@127.0.0.1:5432/ganapp?sslmode=disable"
	// Connect to database
	db, _ := sql.Open("postgres", connStr)

	instance := Database{
		db: db,
	}

	return instance
	// if err != nil {
	// 	// log.Fatal(err)
	// 	fmt.Print("fail connecting")
	// 	fmt.Print(string(err.Error()))
	// }

	// getHandler := http.HandlerFunc(func(w http.ResponseWriter, request *http.Request) {
	// 	get(w, request, db)
	// })

	// http.Handle("/", getHandler)

	// fmt.Println("Listening")
	// http.ListenAndServe(":8080", nil)
}

// func get(w http.ResponseWriter, request *http.Request, db *sql.DB) {
// 	var thisRow string
// 	var thisRow1 string
// 	var allRows []string

// 	rows, err := db.Query("SELECT * FROM users")
// 	if err != nil {
// 		fmt.Print(string(err.Error()))
// 		fmt.Print("fail to select")
// 		return
// 	}
// 	defer rows.Close()
// 	for rows.Next() {
// 		rows.Scan(&thisRow, &thisRow1)
// 		allRows = append(allRows, thisRow1)
// 		fmt.Println(thisRow)
// 		fmt.Println(thisRow1)
// 	}

// 	resp := make(map[int]string)
// 	for i := 0; i < len(allRows); i++ {
// 		resp[i] = allRows[i]
// 	}

// 	json, _ := json.Marshal(resp)
// 	w.Header().Set("Content-Type", "application/json")
// 	// w.Header().Set("Content-Type", "application/octet-stream")
// 	w.Write(json)
// }
