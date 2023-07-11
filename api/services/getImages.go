package services

import (
	"fmt"
	"os"

	"github.com/KauaOrtiz/Ganapp/tree/master/api/database"
	_ "github.com/lib/pq"
)

func GetUserImages(userName string, repository *database.Database) ([][]byte, []string, string, error) {
	imagesPath, imagesClass, message, err := repository.GetUserImages(userName)
	if err != nil {
		return nil, nil, message, err
	}

	var images [][]byte
	var classes []string
	for i := 0; i < len(imagesPath); i++ {
		tmpImage, err := os.ReadFile("../files/output/" + imagesPath[i])

		if err != nil {
			fmt.Println("SERVICE: Failed to load image => ", imagesPath[i])
			continue
		}

		images = append(images, tmpImage)
		classes = append(classes, imagesClass[i])
	}

	return images, classes, "", nil
}
