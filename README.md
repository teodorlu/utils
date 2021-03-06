# Utils

A collection of Clojure stuff. Published on Github to make it simple to run
directly with CLJ.

## Calendar filling

Fill Org-mode journal month.

To use, add this alias to your `~/.clojure/deps.edn`:

```clj
            :fill-month
            {:extra-deps {eu.teod/utils {:git/url "https://github.com/teodorlu/utils.git"
                                         :sha "1617613d356930be56a883abe2cf7f196e0e8a68"}}
             :main-opts ["-m" "eu.teod.utils.fill-month"]}
```

It belongs under the global `:aliases` key.



Generate for todays month:

```bash
$ clj -A:fill-month
```

Generate for the month 1 day from now:

```bash
$ clj -A:fill-month 1
```

With the alias setup, the script can be run simply from the command line:

```
$ # As of January 2020. January 1st is a Wednesday.
$ clj -A:fill-month
** 01 Wednesday
** 02 Thursday
** 03 Friday
** 04 Saturday
** 05 Sunday
** 06 Monday
** 07 Tuesday
** 08 Wednesday
** 09 Thursday
** 10 Friday
** 11 Saturday
** 12 Sunday
** 13 Monday
** 14 Tuesday
** 15 Wednesday
** 16 Thursday
** 17 Friday
** 18 Saturday
** 19 Sunday
** 20 Monday
** 21 Tuesday
** 22 Wednesday
** 23 Thursday
** 24 Friday
** 25 Saturday
** 26 Sunday
** 27 Monday
** 28 Tuesday
** 29 Wednesday
** 30 Thursday
** 31 Friday
```

Run it with something like this:

    clj -A:fill-month >> ~/kb/personal/journal.org
