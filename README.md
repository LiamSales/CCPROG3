# CCPROG3
Object-Oriented Programming

# [UML Diagram](https://lucid.app/lucidchart/fbc7cfde-8654-4c36-98f2-d7ff61862d01/edit?invitationId=inv_f2a9a539-0733-4d69-9f91-b3cb97769b0d) Updated December 19, 2025


Program Overview – Vending Machine Factory Simulator
Vending or “automatic retailing” has its roots traced back as far as 215BC. However, it was only in the
early 1880s that the first commercial coin-operated vending machines were introduced in London,
England which dispensed postcards. Now, modern vending machines offer more variety of products
ranging from drinks, tickets, snacks, WIFI connections, and many more. In Japan, vending machines are
designed to function during blackouts, particularly in the wake of earthquakes and aftershocks, thus
utilizing vending machines as an emergency aid.
In an effort to invoke an appreciation of the many fascinating automation wonders of our modern era, you
will re-create a vending machine. The final product for your project is to create a program that simulates a
Vending Machine Factory. In your menu-based program, there must be options to do the following until
the user chooses to exit the program:
1. Create a Vending Machine
In this feature, the user is asked to choose to create either a Regular or a Special Vending Machine.
Descriptions of the basic attributes and behaviors of both vending machines are described below.
For this option, the program does not need to maintain any previously created Vending Machines.
2. Test a Vending Machine
In this feature, the user is asked to choose to either test the Vending Features or the Maintenance
Features of the current vending machine (i.e., the most recent that was created). When Vending
Features are chosen, the different options are tested until the user chooses to end Vending
Features Test, which will then bring the user back to the Test a Vending Machine menu options.
The same is done when the user chooses Maintenance Features.
3. Exit
In this feature, the program terminates properly.
A Regular Vending Machine consists of item slots that act as an interface for the user to know what is
available for purchase. Each slot is mapped to a specific item and – to keep things simple – kindly assume
that items stored in the slots are unique from each other. The vending machine does not hold infinite
items, so there is a common limit for the number of items that can be stocked. For the project, there must
be at least eight (8) slots and the vending machine should have a capacity of at least ten (10) items per
slot1. The availability of an item should be obvious to the user. In terms of vending features, the machine
should be allowed to receive payment from the user in different denominations2, dispense the item based
on the choice of the user, and produce change. Note that a user may proceed directly with producing
change and skip choosing an item – as if they changed their mind about making a purchase. Additionally,
if there is not enough change in the machine, a transaction should not take place and the user should be
informed of the issue. Note that all vending machines must also inform the user of the amount of calories
found in the item. Lastly, as vending machines do not hold infinite items, you may assume that the
owner/operator of the vending machine regularly performs maintenance. Maintenance features include
restocking/stocking specific items, setting the price of each item type, collecting the payment/money, and
replenishing the money (for different denominations) that will be used by the machine to provide change.
Also, the vending machine has the capability of printing a summary of transactions. In other words, the
vending machine should at least list the quantity of each item sold and the total amount collected in the
sales starting from the previous stocking. This implies that there should also be a display of the starting
inventory and the ending inventory from the last restocking.
Special Vending Machines can also be produced by the simulation program. These machines are special
because, apart from the features of a regular vending machine, the machine can also prepare a selected
product based on (a) the items that are stored in the machine and (b) the choices of items for the product
that the user wants. This means that the amount of calories for the final product is the combination of the
calorie count of each chosen item to include (which might involve more than just addition). Note that since
this is a simulation of the machine's work, your program will display how the final product is “prepared”.
1 You may choose to make the capacity of the vending machine larger, but the vending machine must not have smaller numbers
than indicated.
2 As if the user were inserting coins or bills of different denominations into the machine.
As an example, imagine a vending machine that can dispense a customizable ramen, on top of other
items. The owner of this particular ramen vending machine (RVM) will also set the items that can be
chosen in the customizable ramen and the prices for each. In this case, items that can be included in the
customizable ramen are noodles, egg, chashu pork, fried tofu, negi, tonkotsu broth, ukokkei broth, miso
broth, and shio broth. Please note that another owner may choose to stock the following (on top of what is
previously indicated): different types of noodles, boiled chicken slices, fish cake, and black garlic oil.
Keeping the RVM example in mind, we can imagine that a customer can order the following customized
ramen: 1 order of noodles, 2 orders of chasyu pork, and 1 order of tonkotsu broth. During the preparation,
the machine will display something like this:
Blanching noodles…
Heating broth…
Placing noodles in cup…
Topping with chashu pork…
Pouring broth…
Ramen Done!
Another customer may order a customizable ramen having 2 orders of noodles, 1 order of aji tamago, fried
tofu, and miso broth and the display will be something like this:
Blanching noodles…
Heating broth…
Putting noodles in cup…
Topping with fried tofu and aji tamago…
Pouring broth…
Ramen Done!
It should be noted that another customer may only order chashu pork from the RVM. In this case, this is
treated as an item. Therefore no “preparation” is necessary and the item is dispensed directly. However,
some items that do not make sense to stand alone as a food item should not be allowed to be sold
separately. For example, black garlic oil may not be chosen as an item and is only available when choosing
add-ons to the customizable ramen.
As is the practice, one transaction in either of the vending machines will dispense one (1) item only.

The teacher may choose to dictate the customizable products or allow the student to decide for
themselves. Some other suggested customizable products:
1. Sisig3
2. Silog meals
3. Pinoy burger
4. Halo-halo
5. Ice scramble
Please take note, however, that the special vending machine should have products that need to be
assembled using other items, items that can be purchased individually, and items that are not meant to
be sold to the user.
