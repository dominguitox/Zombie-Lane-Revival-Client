package com.dchoc.idead.items;

import com.dchoc.collection.HashTable;
import com.dchoc.idead.player.Inventory;
import com.dchoc.idead.player.PlayerProfile;
import com.dchoc.toolkit.CsvIDs;
import com.dchoc.toolkit.DChocByteArray;
import com.dchoc.toolkit.Toolkit;
import com.dchoc.toolkit.ToolkitHelpers;
import java.util.Vector;

public class ItemManager implements CsvIDs {
   public static final int INITIAL_COLLECTION_CAPACITY = 10;
   public static final int INITIAL_ITEM_CAPACITY = 48;
   public static final int INITIAL_ITEM_TYPE_CAPACITY = 16;
   public static final int ITEM_NONE = 0;
   public static Vector smCollections;
   public static HashTable smItems;

   public static void init() {
      smItems = new HashTable(16);
      smCollections = new Vector(10);

      for (int i = 0; i < Item.TYPES.length; i++) {
         smItems.add(Item.TYPES[i], new Vector(48));
      }

      loadData();
   }

   private static void loadData() {
      DChocByteArray data = Toolkit.getResourceByteArray(80459);

      try {
         while (true) {
            DecorationItem item = new DecorationItem(data);
            getItems(item.getType()).addElement(item);
         }
      } catch (Exception exception) {
         System.err.println(exception);
         data = Toolkit.getResourceByteArray(80457);

         try {
            while (true) {
               ConstructionItem item = new ConstructionItem(data);
               getItems(item.getType()).addElement(item);
            }
         } catch (Exception exceptionx) {
            System.err.println(exceptionx);
            data = Toolkit.getResourceByteArray(80461);

            try {
               while (true) {
                  ZombieItem item = new ZombieItem(data);
                  getItems(item.getType()).addElement(item);
               }
            } catch (Exception exceptionxx) {
               System.err.println(exceptionxx);
               data = Toolkit.getResourceByteArray(80462);

               try {
                  while (true) {
                     DestructibleItem item = new DestructibleItem(data);
                     getItems(item.getType()).addElement(item);
                  }
               } catch (Exception exceptionxxx) {
                  System.err.println(exceptionxxx);
                  data = Toolkit.getResourceByteArray(80467);

                  try {
                     while (true) {
                        AvatarItem item = new AvatarItem(data);
                        getItems(item.getType()).addElement(item);
                     }
                  } catch (Exception exceptionxxxx) {
                     System.err.println(exceptionxxxx);
                     data = Toolkit.getResourceByteArray(80455);

                     try {
                        while (true) {
                           EdibleItem item = new EdibleItem(data);
                           getItems(item.getType()).addElement(item);
                        }
                     } catch (Exception exceptionxxxxx) {
                        System.err.println(exceptionxxxxx);
                        data = Toolkit.getResourceByteArray(80465);

                        try {
                           while (true) {
                              WeaponItem item = new WeaponItem(data);
                              getItems(item.getType()).addElement(item);
                           }
                        } catch (Exception exceptionxxxxxx) {
                           System.err.println(exceptionxxxxxx);
                           data = Toolkit.getResourceByteArray(80453);

                           try {
                              while (true) {
                                 PlayerItem item = new PlayerItem(data);
                                 getItems(item.getType()).addElement(item);
                              }
                           } catch (Exception exceptionxxxxxxx) {
                              System.err.println(exceptionxxxxxxx);
                              data = Toolkit.getResourceByteArray(80463);

                              try {
                                 while (true) {
                                    CollectibleItem item = new CollectibleItem(data);
                                    getItems(item.getType()).addElement(item);
                                 }
                              } catch (Exception exceptionxxxxxxxx) {
                                 System.err.println(exceptionxxxxxxxx);
                                 data = Toolkit.getResourceByteArray(80456);

                                 try {
                                    while (true) {
                                       FarmingItem item = new FarmingItem(data);
                                       getItems(item.getType()).addElement(item);
                                    }
                                 } catch (Exception exceptionxxxxxxxxx) {
                                    System.err.println(exceptionxxxxxxxxx);
                                    data = Toolkit.getResourceByteArray(80460);

                                    try {
                                       while (true) {
                                          FenceItem item = new FenceItem(data);
                                          getItems(item.getType()).addElement(item);
                                       }
                                    } catch (Exception exceptionxxxxxxxxxx) {
                                       System.err.println(exceptionxxxxxxxxxx);
                                       data = Toolkit.getResourceByteArray(80458);

                                       try {
                                          while (true) {
                                             CraftingItem item = new CraftingItem(data);
                                             getItems(item.getType()).addElement(item);
                                          }
                                       } catch (Exception exceptionxxxxxxxxxxx) {
                                          System.err.println(exceptionxxxxxxxxxxx);
                                          data = Toolkit.getResourceByteArray(80464);

                                          try {
                                             while (true) {
                                                GiftItem item = new GiftItem(data);
                                                getItems(item.getType()).addElement(item);
                                             }
                                          } catch (Exception exceptionxxxxxxxxxxxx) {
                                             System.err.println(exceptionxxxxxxxxxxxx);
                                             data = Toolkit.getResourceByteArray(80454);

                                             try {
                                                while (true) {
                                                   BoosterItem item = new BoosterItem(data);
                                                   getItems(item.getType()).addElement(item);
                                                }
                                             } catch (Exception exceptionxxxxxxxxxxxxx) {
                                                System.err.println(exceptionxxxxxxxxxxxxx);
                                                data = Toolkit.getResourceByteArray(80441);

                                                try {
                                                   while (true) {
                                                      smCollections.addElement(new Collection(data));
                                                   }
                                                } catch (Exception exceptionxxxxxxxxxxxxxx) {
                                                   System.err.println(exceptionxxxxxxxxxxxxxx);
                                                }
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static Item getItem(int type, int id) {
      if (id == -1) {
         return null;
      }

      Vector items = getItems(type);
      if (items != null) {
         for (int i = 0; i < items.size(); i++) {
            Item item = (Item)items.elementAt(i);
            if (item.getID() == id) {
               return item;
            }
         }
      }

      return null;
   }

   public static Vector getItems(int type) {
      return type == -1 ? null : (Vector)smItems.get(type);
   }

   public static Item getItem(int id) {
      if (id == -1) {
         return null;
      }

      for (int i = 0; i < Item.TYPES.length; i++) {
         Vector items = getItems(Item.TYPES[i]);
         if (items != null) {
            for (int j = 0; j < items.size(); j++) {
               Item item = (Item)items.elementAt(j);
               if (item.getID() == id) {
                  return item;
               }
            }
         }
      }

      return null;
   }

   public static WeaponItem getWeapon(int id) {
      return (WeaponItem)getItem(1907, id);
   }

   public static AvatarItem getAvatar(int id) {
      return (AvatarItem)getItem(1911, id);
   }

   public static DecorationItem getDecoration(int id) {
      return (DecorationItem)getItem(405, id);
   }

   public static DestructibleItem getDestructible(int id) {
      return (DestructibleItem)getItem(50, id);
   }

   public static ZombieItem getZombie(int id) {
      return (ZombieItem)getItem(50, id);
   }

   public static PlayerItem getPlayer(int id) {
      return (PlayerItem)getItem(837, id);
   }

   public static CollectibleItem getCollectible(int id) {
      return (CollectibleItem)getItem(47, id);
   }

   public static CraftingItem getCrafting(int id) {
      return (CraftingItem)getItem(1913, id);
   }

   public static GiftItem getGift(int id) {
      return (GiftItem)getItem(1909, id);
   }

   public static BoosterItem getBooster(int id) {
      return (BoosterItem)getItem(3270, id);
   }

   public static Item calculateItem(int type, int[][] weights) {
      if (weights == null) {
         return null;
      }

      int total = 0;

      for (int i = 0; i < weights.length; i++) {
         total += weights[i][1];
      }

      int random = ToolkitHelpers.getRandomInt(0, total);
      int sum = 0;
      int id = 0;
      boolean found = false;

      for (int i = 0; i < weights.length; i++) {
         if (sum <= random && random <= weights[i][1] + sum) {
            id = weights[i][0];
            found = true;
            break;
         }

         sum += weights[i][1];
      }

      if (!found || id == 0) {
         return null;
      } else {
         return type == -1 ? getItem(id) : getItem(type, id);
      }
   }

   public static Collection getCollection(CollectibleItem item) {
      if (item == null) {
         return null;
      }

      for (int i = 0; i < smCollections.size(); i++) {
         Collection collection = (Collection)smCollections.elementAt(i);
         int[] items = collection.getItems();
         int count = items == null ? 0 : items.length;

         for (int j = 0; j < count; j++) {
            if (item.getID() == items[j]) {
               return collection;
            }
         }
      }

      return null;
   }

   public static int getCollectionAmount(Collection collection) {
      if (collection == null) {
         return 0;
      }

      Inventory inventory = PlayerProfile.getInventory();
      int[] items = collection.getItems();
      int amount = inventory.getItemCount(items[0]);

      for (int i = 1; i < items.length; i++) {
         amount = Math.min(amount, inventory.getItemCount(items[i]));
      }

      return amount;
   }

   public static int getCraftableAmount(CraftingItem item) {
      if (item == null) {
         return 0;
      }

      Inventory inventory = PlayerProfile.getInventory();
      int[] items = item.getCollectibleItems();
      int amount = inventory.getItemCount(items[0]);

      for (int i = 1; i < items.length; i++) {
         amount = Math.min(amount, inventory.getItemCount(items[i]));
      }

      return amount;
   }
}
