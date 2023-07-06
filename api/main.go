package main

import (
	"encoding/json"
	"fmt"
	"net/http"
	"os"

	"github.com/KauaOrtiz/Ganapp/tree/master/api/database"
	"github.com/KauaOrtiz/Ganapp/tree/master/api/services"
)

var db database.Database
var connectionErr error

func main() {
	db, connectionErr = database.GetInstance()

	if connectionErr != nil {
		fmt.Println("Stoping server due to connection error => ", connectionErr.Error())
		return
	}

	createImageHandler := http.HandlerFunc(createImage)
	getImageHandler := http.HandlerFunc(getImage)
	getUserImagesHandler := http.HandlerFunc(getUserImages)
	createUserHandler := http.HandlerFunc(createUser)
	loginUserHandler := http.HandlerFunc(loginUser)

	http.Handle("/createImage", createImageHandler)
	http.Handle("/getUserImages", getUserImagesHandler)
	http.Handle("/getImage", getImageHandler)
	http.Handle("/createUser", createUserHandler)
	http.Handle("/loginUser", loginUserHandler)

	fmt.Println("Go server listening on port 8080 🚀")
	http.ListenAndServe(":8080", nil)
}

func createUser(w http.ResponseWriter, request *http.Request) {
	var user database.User
	err := json.NewDecoder(request.Body).Decode(&user)

	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		w.WriteHeader(http.StatusBadRequest)
	}

	message, err := services.CreateUser(user.Name, user.Password, &db)

	if err != nil {
		http.Error(w, message, http.StatusBadRequest)
		w.WriteHeader(http.StatusBadRequest)
	}
	w.Write([]byte(message))
}

func loginUser(w http.ResponseWriter, request *http.Request) {
	var user database.User
	err := json.NewDecoder(request.Body).Decode(&user)

	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	fmt.Println(user.Password)

	message, loggedUser, err := services.LoginUser(user.Name, user.Password, &db)

	if err != nil {
		http.Error(w, message, http.StatusBadRequest)
		return
	}

	response := make(map[string]string)
	response["name"] = loggedUser.Name
	response["password"] = loggedUser.Password

	json, _ := json.Marshal(response)

	w.Write([]byte(json))
}

func createImage(w http.ResponseWriter, request *http.Request) {
    fmt.Println("1")
	err := request.ParseMultipartForm(32 << 20) // maxMemory 32MB
	if err != nil {
		w.WriteHeader(http.StatusBadRequest)
		return
	}
    fmt.Println("2")

	//Access the photo key - First Approach
	file, header, err := request.FormFile("photo")
	if err != nil {
		w.WriteHeader(http.StatusBadRequest)
		return
	}

	userName := request.FormValue("userName")
	imgName := header.Filename

	newImg, message, err := services.CreateImage(file, imgName, userName, &db)

	if err != nil {
		http.Error(w, message, http.StatusBadRequest)
		return
	}

	response := make(map[string][]byte)
	response["image"] = newImg
	response["classification"] = []byte(message)

	json, _ := json.Marshal(response)
	w.Header().Set("Content-Type", "application/json")
	w.Write(json)
}

func getUserImages(w http.ResponseWriter, request *http.Request) {
	fields := request.URL.Query()["name"]

	if len(fields) == 0 {
		http.Error(w, "Could not find user name on URI", http.StatusBadRequest)
		return
	}

	userName := fields[0]
	images, message, err := services.GetUserImages(userName, &db)

	if err != nil {
		http.Error(w, message, http.StatusBadRequest)
		return
	}

	response := make(map[string][]byte)
	for i := 0; i < len(images); i++ {
		key := "image_" + fmt.Sprint(i)
		response[key] = images[i]
	}

	json, _ := json.Marshal(response)
	w.Header().Set("Content-Type", "application/json")
	w.Write(json)
}

func getImage(w http.ResponseWriter, r *http.Request) {
	tmpImg, err := os.ReadFile("../files/tests/house.jpg")

	if err != nil {
		w.WriteHeader(http.StatusInternalServerError)
		return
	}

	response := make(map[string][]byte)
	response["image"] = tmpImg

	json, _ := json.Marshal(response)
	w.Header().Set("Content-Type", "application/json")
	w.Write(json)
}
