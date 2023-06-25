package main

import (
	"database/sql"
	"encoding/json"
	"fmt"
	"net/http"

	_ "github.com/lib/pq"
)

func main() {
	connStr := "postgresql://postgres:ganapppass@127.0.0.1:5432/ganapp?sslmode=disable"
	// Connect to database
	db, err := sql.Open("postgres", connStr)
	if err != nil {
		// log.Fatal(err)
		fmt.Print("fail connecting")
		fmt.Print(string(err.Error()))
	}

	getHandler := http.HandlerFunc(func(w http.ResponseWriter, request *http.Request) {
		get(w, request, db)
	})

	http.Handle("/", getHandler)

	fmt.Println("Listening")
	http.ListenAndServe(":8080", nil)
}

func get(w http.ResponseWriter, request *http.Request, db *sql.DB) {
	var thisRow string
	var thisRow1 string
	var allRows []string

	rows, err := db.Query("SELECT * FROM users")
	defer rows.Close()
	if err != nil {
		fmt.Print(string(err.Error()))
		fmt.Print("fail to select")
		return
	}
	for rows.Next() {
		rows.Scan(&thisRow, &thisRow1)
		allRows = append(allRows, thisRow1)
		fmt.Println(thisRow)
		fmt.Println(thisRow1)
	}

	resp := make(map[int]string)
	for i := 0; i < len(allRows); i++ {
		resp[i] = allRows[i]
	}

	json, _ := json.Marshal(resp)
	w.Header().Set("Content-Type", "application/json")
	// w.Header().Set("Content-Type", "application/octet-stream")
	w.Write(json)
}
