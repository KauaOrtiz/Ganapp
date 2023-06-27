package services

import (
	"database/sql"
	"encoding/json"

	_ "github.com/lib/pq"
)

func GetUserImages(db *sql.DB) ([]byte, error) {

	resp := make(map[int]string)

	json, _ := json.Marshal(resp)

	return json, nil
}
