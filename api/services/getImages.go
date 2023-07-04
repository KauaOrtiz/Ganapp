package services

import (
	"fmt"
	"os"

	"github.com/KauaOrtiz/Ganapp/tree/master/api/database"
	_ "github.com/lib/pq"
)

func GetUserImages(userName string, repository *database.Database) ([][]byte, string, error) {
	imagesPath, message, err := repository.GetUserImages(userName)
	if err != nil {
		return nil, message, err
	}

	var images [][]byte
	for i := 0; i < len(imagesPath); i++ {
		tmpImage, err := os.ReadFile("../files/input/" + imagesPath[i])

		if err != nil {
			fmt.Println("SERVICE: Failed to load image => ", imagesPath[i])
			continue
		}

		images = append(images, tmpImage)
	}

	return images, "", nil
}
