# Ganapp
This GitHub repository hosts an Android mobile application developed as part of the Programming Languages discipline in the Computer Engineering course in Federal University of Rio Grande - Brazil. The app leverages the power of Generative Adversarial Networks (GANs) to generate synthetic images.

# Ganapp back-end
Our backend consists of two differente services, that work together in order to process, create and retrieve images.

> The first api is written in [Golang](https://go.dev/) and it's goal is to provide a fast connection between out client (app) and the second api, which is responsible for a deeper processing.

> The second api, as told above, performs some hard processing, using the [Tensor flow](https://www.tensorflow.org/?hl=pt-br) api. It's written in [Node.js](https://nodejs.org/en), being resposible for analising images, classfy them and render a same class synthetic image.

For detailed info on how to setup environment for each project, access the `api` and `gan_inference` folders at this project's root. The README.me files explain all you need to know.

