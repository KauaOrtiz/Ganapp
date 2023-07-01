package services

import (
	"encoding/json"
	"fmt"
	"io"
	"mime/multipart"
	"os"

	"net/http"

	"github.com/KauaOrtiz/Ganapp/tree/master/api/database"
	"github.com/google/uuid"
	_ "github.com/lib/pq"
)

type Message struct {
	Classification string `json:"classification"`
}

func CreateImage(file multipart.File, fileName string, userName string, repository *database.Database) ([]byte, string, error) {
	modifiedName := uuid.NewString() + "_" + fileName
	inputPath := "../files/input/" + modifiedName

	tmpfile, err := os.Create(inputPath)

	if err != nil {
		fmt.Println("SERVICE: Failed to create user image. Error => ", err.Error())
		return nil, "Could not save image on database", err
	}
	defer tmpfile.Close()

	_, err = io.Copy(tmpfile, file)
	if err != nil {
		fmt.Println("SERVICE: Failed to handle user image. Error => ", err.Error())
		return nil, "Could not save image on database", err
	}

	url := "http://localhost:3000/process-image?path=" + modifiedName
	response, err := http.Get(url)

	if err != nil {
		fmt.Println("SERVICE: Failed to process user image. Error => ", err.Error())
		return nil, "Could not process image", err
	}

	var decoded Message
	decodeErr := json.NewDecoder(response.Body).Decode(&decoded)
	if decodeErr != nil {
		fmt.Println("SERVICE: Failed to decode node api response. Error => ", err.Error())
		return nil, "Could not decode node api response", err
	}

	// outputPath := "../files/output/" + modifiedName
	outputPath := "../files/output/" + "test.jpg"
	processedFile, err := os.ReadFile(outputPath)
	if err != nil {
		fmt.Println("SERVICE: Failed to return processed image. Error => ", err.Error())
		return nil, "Could not return processed image", err
	}

	message, err := repository.SaveNewImage(userName, modifiedName)
	if err != nil {
		fmt.Println("SERVICE: Failed to process image on database. Error => ", err.Error())
		return nil, message, err
	}

	return processedFile, decoded.Classification, nil
}
