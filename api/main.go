package main

import (
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"os"

	"github.com/KauaOrtiz/Ganapp/tree/master/api/database"
)

func main() {
	db := database.GetInstance()

	user := database.User{
		Name: "João", Password: "senha",
	}

	db.CreateUser(user)

	fmt.Println("Listening")
	createImageHandler := http.HandlerFunc(createImage)
	getHandler := http.HandlerFunc(get)

	http.Handle("/image", createImageHandler)
	http.Handle("/json", getHandler)

	http.ListenAndServe(":8080", nil)
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
	defer tmpfile.Close()
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
	return
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
