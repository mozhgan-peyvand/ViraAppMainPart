#!/bin/sh

echo "Copying pre-commit hook..."
if ! test -f ../../.git/hooks/pre-commit; then
    cp ./pre-commit ../../.git/hooks
    chmod u+x ../../.git/hooks/pre-commit
    echo "Done!"
else
       echo "already exists. Did nothing."
fi
