## Mendix App

#### How to Build

- On the project root directory run `mvn spring-boot:run`
- This will compile the project and starts an embedded tomcat server on port `8080` with rest service deployed

#### Notes

- This project demos developing REST SERVICE using Spring Boot
- This app uses Lucene to index and search documents
- Supports below REST API calls

- *GET ALL Recipes*

   http://localhost:8080/recipes
  
- _Filter based on Index Fields_

    - Index Fields currently supported `title` and `category` 
    - Query supports free text search containing spaces

  http://localhost:8080/recipes?title=Chili&category=Chili
  
  http://localhost:8080/recipes?category=Chili
  
  http://localhost:8080/recipes?category=Main dish
  
- _GET ALL CATEGORIES_

   http://localhost:8080/recipes/categories
  
  
- _ADD NEW RECIPE_ : 
    Validates Input XML and if valid adds to available recipes

   http://localhost:8080/recipes/add

```
<?xml version="1.0" encoding="UTF-8"?>
<recipeml version="0.5">
    <recipe>
        <head>
            <title>Lucene Cake</title>
            <categories>
                <cat>Lucene</cat>
            </categories>
            <yield>1</yield>
        </head>
   </recipe>
</recipeml>
```
