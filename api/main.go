package main

import (
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"os"

	"github.com/KauaOrtiz/Ganapp/tree/master/api/database"
	"github.com/KauaOrtiz/Ganapp/tree/master/api/services"
)

var db database.Database

func main() {
	db = database.GetInstance()

	fmt.Println("Listening")
	// createImageHandler := http.HandlerFunc(createImage)
	// getHandler := http.HandlerFunc(get)
	getImageHandler := http.HandlerFunc(getImage)

	// createUserHandler := http.HandlerFunc(createUser)
	// loginUserHandler := http.HandlerFunc(loginUser)

	// http.Handle("/image", createImageHandler)
	// http.Handle("/json", getHandler)
	http.Handle("/getImage", getImageHandler)

	// http.Handle("/createUser", createUserHandler)
	// http.Handle("/loginUser", loginUserHandler)

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
	err := request.ParseMultipartForm(32 << 20) // maxMemory 32MB
	if err != nil {
		w.WriteHeader(http.StatusBadRequest)
		return
	}
	//Access the photo key - First Approach
	file, h, err := request.FormFile("photo")
	if err != nil {
		w.WriteHeader(http.StatusBadRequest)
		return
	}
	tmpfile, err := os.Create("./" + h.Filename)
	// defer tmpfile.Close()
	if err != nil {
		w.WriteHeader(http.StatusInternalServerError)
		return
	}
	_, err = io.Copy(tmpfile, file)
	if err != nil {
		w.WriteHeader(http.StatusInternalServerError)
		return
	}

	tmpImg, _ := os.ReadFile("./photo.jpg")
	w.Header().Set("Content-Type", "application/octet-stream")
	w.Write(tmpImg)
	fmt.Print("Hereee")
	//w.WriteHeader(200)
}

func get(w http.ResponseWriter, request *http.Request) {
	resp := make(map[string][]byte)

	tmpImg1, _ := os.ReadFile("./<imagem 1>.jpg")
	resp["img1"] = tmpImg1

	tmpImg2, _ := os.ReadFile("./<imagem 2>.jpg")
	resp["img2"] = tmpImg2

	json, _ := json.Marshal(resp)
	w.Header().Set("Content-Type", "application/json")
	// w.Header().Set("Content-Type", "application/octet-stream")
	w.Write(json)
}

func getImage(w http.ResponseWriter, r *http.Request) {
	ImageString, err := os.ReadFile("./testImage.txt")

	if err != nil {
		w.WriteHeader(http.StatusInternalServerError)
		return
	}

	w.Write([]byte(ImageString))
}
