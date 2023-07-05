## Setting environment up
* First, we need [go](https://go.dev/doc/install) isntalled.
* We need a postgres database running on localhost. We can have a docker container running our db or [install postgres itself](https://www.postgresql.org/download/).
* In case you chose docker, install its CLI and use the following command `docker run --name ganapp -e POSTGRES_PASSWORD=ganapppass -d -p 5432:5432 postgres`. Then simply start the container! Igore the next step!
* If not using Docker, create a database called `ganapp` and give the postgres user the password `ganapppass`. We need those values for go-api to connect.
* We now need to execute our schema. Install [golang-migrate](https://github.com/golang-migrate/migrate/blob/master/cmd/migrate/README.md) for next step.
* We need to enter `api` folder and run `migrate -path database/migrations -database "postgresql://postgres:ganapppass@localhost:5432/ganapp?sslmode=disable" -verbose up`
* Last, we need to install dependencies. On `api` folder, run `go mod tidy`
* Now we are all set to run our project  🚀

## Running project
* With out database connected and migrated, we can enter `api` folder and run `go run main.go`
* Make sure to create a `input` and `output` folder inside the `files` folder, which is on the project root
* There is this route `/createImage` that needs our node api running. Make sure to have it on in case this route will be called.

## Routes
* **http://localhost:8080/createUser**

  We expect a json like `{
	"name": "Rafael",
	"password": "1234"
}`<br>
  Returns a message string
  
* **http://localhost:8080/loginUser** (Not used on app)

  We expect a json like `{
	"name": "Rafael",
	"password": "1234"
}`<br>
  Returns a json like `{
	"name": "Rafael",
	"password": "1234"
}`

* **http://localhost:8080/createImage** 

  We expect a form file like `{
	"photo": FILE,
	"userName": "Rafael"
}`<br>
  Returns a json like a json like `{
	"classification": "A imagem gerada pertence ah classe x",
	"image": "IMAGEM EM Base64"
}`

* **http://localhost:8080/getUserImages?name=Joao** 

  We expect a query param like on shown above <br>
  Returns a json like a json like `{
	"image_0": "IMAGEM EM Base64",
	"image_1": "IMAGEM EM Base64",
	"image_2": "IMAGEM EM Base64"
}`

* **http://localhost:8080/getImage** (Not used on app)

  We expect nothing on body nor query <br>
  Returns a json like a json like `{
	"image": "IMAGEM TESTS EM Base64"
}`
