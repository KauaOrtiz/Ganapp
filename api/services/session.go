package services

import (
	"errors"
	"fmt"
	"strings"

	"github.com/KauaOrtiz/Ganapp/tree/master/api/database"

	_ "github.com/lib/pq"
)

func CreateUser(name string, password string, repository *database.Database) (string, error) {
	user := database.User{
		Name:     name,
		Password: password,
	}

	foundUser, err := repository.UserExistsByName(name)

	if err != nil {
		fmt.Println("SERVICE: Failed to create user.")
		return "Failed to create user", err
	}

	if foundUser {
		fmt.Println("SERVICE: Failed to create user. A user with provided name already exists.")
		return "A user with provided name already exists.", err
	}

	status, err := repository.CreateUser(user)

	if err != nil && status {
		fmt.Println("SERVICE: Failed to create user.")
		return "Failed to create user", err
	}

	return "User created sucessfully", nil
}

func LoginUser(name string, passwordd string, repository *database.Database) (string, database.User, error) {
	user, err := repository.GetUserByName(name)

	if err != nil {
		fmt.Println("SERVICE: Failed to login user.")
		return "Failed to login user", user, err
	}

	fmt.Println(user.Password)
	fmt.Println(passwordd)

	if user.Password != passwordd {
		fmt.Println("SERVICE: Failed to login user. Credentials do not match.")
		msg := "Failed to login. Check credentials."
		return msg, user, errors.New(strings.ToLower(msg))
	}

	return "Login was successful", user, nil

}
