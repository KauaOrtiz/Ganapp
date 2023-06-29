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
	FindUserByName(name string) (bool, error)
	GetUserByName(name string) (bool, error)
	LoginUser(user User) (User, error)
	GetUserImages(userName string) ([]string, error)
	SaveNewImage(imagePath string) (bool, error)
}

type Database struct {
	Db *sql.DB
}

func (db Database) CreateUser(user User) (bool, error) {
	_, err := db.Db.Query("INSERT INTO users (name, password) VALUES ($1, $2)", user.Name, user.Password)

	if err != nil {
		fmt.Print("Failed to create user. Error => ", err.Error())
		return false, err
	}

	return true, nil
}

func (db Database) UserExistsByName(name string) (bool, error) {
	rows, err := db.Db.Query("SELECT id FROM users WHERE name = $1", name)

	if err != nil {
		fmt.Println("DB: Failed to search for user by name. Error => ", err.Error())
		return false, err
	}

	defer rows.Close()
	if rows.Next() {
		fmt.Println("DB: Can not create user with existent name")
		return true, nil
	}

	return false, nil
}

func (db Database) GetUserByName(name string) (User, error) {
	rows, err := db.Db.Query("SELECT password FROM users WHERE name = $1", name)

	var user User
	var password string

	if err != nil {
		fmt.Println("DB: Failed to search for user by name. Error => ", err.Error())
		return user, err
	}

	defer rows.Close()
	if !rows.Next() {
		fmt.Println("DB: User does not exist")
		return user, nil
	}

	rows.Scan(&password)

	user.Name = name
	user.Password = password

	return user, nil
}

func (dbInstance Database) GetUserImages(userName string) ([]string, error) {
	var row string
	var userFilesPaths []string

	rows, err := dbInstance.Db.Query("SELECT * FROM users")

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
		Db: db,
	}

	return instance
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
