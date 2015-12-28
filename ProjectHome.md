### Radix Tree Features of this Implementation ###
  * Utilizes loops instead of recursion for hopefully faster searches.
  * Almost all actions use a single common search function.
  * Tree is sorted in lexilogical (alphabetical) order.
  * Support for special handling for duplicate keys - good for keeping a count or list of values for a given key, etc.
  * Support for not continuing an insert after the search phase - good for tree reduction/simplification for example where all keys at a level have the same value.

Some core logic was derived from the project at http://code.google.com/p/radixtree/

This class was developed for a particular project that needed prefix searching with support for custom duplicate key handling combining values, reading/writing the tree to disk, and handling to do reduction of common values. Contact project owner if interested in classes that extend for read/write or iteration.
This base class is designed for basic functionality and expansion for purposes listed above and others.

This implementation has not been highly tested. Primary testing has been for prefix matching and dealing with duplicate keys.
Please share any test results, patches, uses, or other contributions.