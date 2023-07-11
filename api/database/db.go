package database

import (
	"database/sql"
	"errors"
	"fmt"

	"github.com/KauaOrtiz/Ganapp/tree/master/api/models"
	_ "github.com/lib/pq"
)

type User = models.User

type Repository interface {
	CreateUser(user User) (bool, error)
	LoginUser(user User) (User, error)
	FindUserByName(name string) (User, error)
	UserExistsByName(name string) (bool, error)
	GetUserImages(userName string) ([]string, string, error)
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
	rows, err := db.Db.Query("SELECT id, password FROM users WHERE name = $1", name)

	var user User
	var id string
	var password string

	if err != nil {
		fmt.Println("DB: Failed to search for user by name. Error => ", err.Error())
		return user, err
	}

	defer rows.Close()
	if !rows.Next() {
		fmt.Println("DB: User does not exist")
		return user, errors.New("User does not exist")
	}

	rows.Scan(&id, &password)

	user.Id = id
	user.Name = name
	user.Password = password

	return user, nil
}

func (db Database) GetUserImages(userName string) ([]string, []string, string, error) {
	var path, class string
	var userFilesPath, userFilesClass []string

	user, err := db.GetUserByName(userName)

	if err != nil {
		fmt.Println("DB: Failed save user image. Error => ", err.Error())
		return nil, nil, "Could not save image on database", err
	}

	rows, err := db.Db.Query("SELECT path, classification FROM images WHERE user_id = $1", user.Id)

	if err != nil {
		fmt.Println("DB: Failed to search for user by name. Error => ", err.Error())
		return nil, nil, "Failed to get user images", err
	}
	defer rows.Close()

	for rows.Next() {
		rows.Scan(&path, &class)
		userFilesPath = append(userFilesPath, path)
		userFilesClass = append(userFilesClass, class)
	}

	return userFilesPath, userFilesClass, "", nil
}

func (db Database) SaveNewImage(userName string, imgPath string, class string) (string, error) {
	user, err := db.GetUserByName(userName)

	if err != nil {
		fmt.Println("DB: Failed save user image. Error => ", err.Error())
		return "Could not save image on database", err
	}

	rows, err := db.Db.Query("INSERT INTO images (path, user_id, classification) VALUES ($1, $2, $3)", imgPath, user.Id, class)

	if err != nil {
		fmt.Println("DB: Failed save user image. Error => ", err.Error())
		return "Could not save image on database", err
	}
	defer rows.Close()

	return "Image was saved successfully", nil
}

func GetInstance() (Database, error) {
	connStr := "postgresql://postgres:senha@127.0.0.1:5432/ganapp?sslmode=disable"

	// Connect to database
	db, err := sql.Open("postgres", connStr)
	if err != nil {
		fmt.Println("DB: Failed to connect to database. Error => ", err.Error())
		return Database{}, err
	}

	instance := Database{
		Db: db,
	}

	return instance, nil
}
