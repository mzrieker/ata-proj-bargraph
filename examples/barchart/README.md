# ATA-Project Bargraph

Example barchart using ezd3's D3 svg component. Reading JSON data from an online bin.

## Run

    lein figwheel

and open your browser at [localhost:3449](http://localhost:3449/).

## Dependencies

This example was built using Java 9.  If you're using Java 8 or prior,
you will want to comment out the following line in `project.clj`.

```clojure
:jvm-opts ["--add-modules" "java.xml.bind"]

```

## License

Based heavily on an example that is Copyright © 2018 Hitesh Jasani
