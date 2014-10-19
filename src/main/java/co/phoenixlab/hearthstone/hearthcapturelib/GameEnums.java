/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Vincent Zhang
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package co.phoenixlab.hearthstone.hearthcapturelib;

import co.phoenixlab.hearthstone.hearthcapturelib.packets.*;

/**
 * A collection of enumerations related to Hearthstone.
 * <p>
 * Many of these were provided by Hearthy <https://github.com/6f/Hearthy>
 */
public final class GameEnums {

    /**
     * An enumeration with unique IDs.
     */
    public interface IDEnum {
        int getId();
    }


    private static <R extends Enum & IDEnum> R getIDEnum(Class<R> enumClass, int i) {
        for (R r : enumClass.getEnumConstants()) {
            if (r.getId() == i) {
                return r;
            }
        }
        return null;
    }

    /**
     * Gets an enum value based on the unique ID value. If the Enum is an IDEnum, it will use the assigned ID instead of the ordinal value.
     *
     * @param enumClass The type of enum.
     * @param i         The ID of the enum to get.
     * @param <R>       Type parameter of the enum class.
     * @return The enum value matching ID within enumClass, or null if not found.
     */
    @SuppressWarnings("unchecked")
    public static <R extends Enum> R getById(Class<R> enumClass, int i) {
        for (Class c : enumClass.getInterfaces()) {
            if (c.equals(IDEnum.class)) {
                //  Lots of type erasure, but we confirmed that we are 1) an enum 2) an IDEnum
                return (R) getIDEnum((Class) enumClass, i);
            }
        }
        R[] r = enumClass.getEnumConstants();
        if (i < 0 || i >= r.length) {
            return null;
        }
        return r[i];
    }

    /**
     * Data types used in the network protocol.
     */
    public enum DataType {
        ENUM(false, true),
        INT,
        BOOL(false, true),
        INT32(false, true),
        UINT32(false, true),
        INT64(false, true),
        UINT64(false, true),
        FIXED32,
        BYTES(true, false),
        STRING(true, false),
        STRUCT(true, false);

        public final boolean lengthDelimited;
        public final boolean varInt;

        private DataType() {
            this(false, false);
        }

        private DataType(boolean lengthDelimited, boolean varInt) {
            this.lengthDelimited = lengthDelimited;
            this.varInt = varInt;
        }

        public boolean isLengthDelimited() {
            return lengthDelimited;
        }

        public boolean isVarInt() {
            return varInt;
        }
    }


    /**
     * Various game tag enums. Many are self-explanatory and relate to game events or card attributes.
     */
    public enum GameTag
            implements IDEnum {
        IGNORE_DAMAGE(1),
        MISSION_EVENT(6),
        TIMEOUT(7),
        TURN_START(8),
        TURN_TIMER_SLUSH(9),
        PREMIUM(12),
        GOLD_REWARD_STATE(13),
        PLAYSTATE(17),
        LAST_AFFECTED_BY(18),
        STEP(19),
        TURN(20),
        FATIGUE(22),
        CURRENT_PLAYER(23),
        FIRST_PLAYER(24),
        RESOURCES_USED(25),
        RESOURCES(26),
        HERO_ENTITY(27),
        MAX_HAND_SIZE(28),
        START_HAND_SIZE(29),
        PLAYER_ID(30),
        TEAM_ID(31),
        TRIGGER_VISUAL(32),
        RECENTLY_ARRIVED(33),
        PROTECTING(34),
        PROTECTED(35),
        DEFENDING(36),
        PROPOSED_DEFENDER(37),
        ATTACKING(38),
        PROPOSED_ATTACKER(39),
        ATTACHED(40),
        EXHAUSTED(43),
        DAMAGE(44),
        HEALTH(45),
        ATK(47),
        COST(48),
        ZONE(49),
        CONTROLLER(50),
        OWNER(51),
        DEFINITION(52),
        ENTITY_ID(53),
        ELITE(114),
        MAX_RESOURCES(176),
        CARD_SET(183),
        CARD_TEXT_IN_HAND(184),
        CARD_NAME(185),
        CARD_ID(186),
        DURABILITY(187),
        SILENCED(188),
        WINDFURY(189),
        TAUNT(190),
        STEALTH(191),
        SPELL_POWER(192),
        DIVINE_SHIELD(194),
        CHARGE(197),
        NEXT_STEP(198),
        CLASS(199),
        CARD_RACE(200),
        FACTION(201),
        CARD_TYPE(202),
        RARITY(203),
        STATE(204),
        SUMMONED(205),
        FREEZE(208),
        ENRAGED(212),
        RECALL(215),
        LOYALTY(216),
        DEATH_RATTLE(217),
        BATTLECRY(218),
        SECRET(219),
        COMBO(220),
        CANT_HEAL(221),
        CANT_DAMAGE(222),
        CANT_SET_ASIDE(223),
        CANT_REMOVE_FROM_GAME(224),
        CANT_READY(225),
        CANT_EXHAUST(226),
        CANT_ATTACK(227),
        CANT_TARGET(228),
        CANT_DESTROY(229),
        CANT_DISCARD(230),
        CANT_PLAY(231),
        CANT_DRAW(232),
        INCOMING_HEALING_MULTIPLIER(233),
        INCOMING_HEALING_ADJUSTMENT(234),
        INCOMING_HEALING_CAP(235),
        INCOMING_DAMAGE_MULTIPLIER(236),
        INCOMING_DAMAGE_ADJUSTMENT(237),
        INCOMING_DAMAGE_CAP(238),
        CANT_BE_HEALED(239),
        CANT_BE_DAMAGED(240),
        CANT_BE_SET_ASIDE(241),
        CANT_BE_REMOVED_FROM_GAME(242),
        CANT_BE_READIED(243),
        CANT_BE_EXHAUSTED(244),
        CANT_BE_ATTACKED(245),
        CANT_BE_TARGETED(246),
        CANT_BE_DESTROYED(247),
        CANT_BE_SUMMONING_SICK(253),
        FROZEN(260),
        JUST_PLAYED(261),
        LINKED_CARD(262),
        ZONE_POSITION(263),
        CANT_BE_FROZEN(264),
        COMBO_ACTIVE(266),
        CARD_TARGET(267),
        NUM_CARDS_PLAYED_THIS_TURN(269),
        CANT_BE_TARGETED_BY_OPPONENTS(270),
        NUM_TURNS_IN_PLAY(271),
        NUM_TURNS_LEFT(272),
        OUTGOING_DAMAGE_CAP(273),
        OUTGOING_DAMAGE_ADJUSTMENT(274),
        OUTGOING_DAMAGE_MULTIPLIER(275),
        OUTGOING_HEALING_CAP(276),
        OUTGOING_HEALING_ADJUSTMENT(277),
        OUTGOING_HEALING_MULTIPLIER(278),
        INCOMING_ABILITY_DAMAGE_ADJUSTMENT(279),
        INCOMING_COMBAT_DAMAGE_ADJUSTMENT(280),
        OUTGOING_ABILITY_DAMAGE_ADJUSTMENT(281),
        OUTGOING_COMBAT_DAMAGE_ADJUSTMENT(282),
        OUTGOING_ABILITY_DAMAGE_MULTIPLIER(283),
        OUTGOING_ABILITY_DAMAGE_CAP(284),
        INCOMING_ABILITY_DAMAGE_MULTIPLIER(285),
        INCOMING_ABILITY_DAMAGE_CAP(286),
        OUTGOING_COMBAT_DAMAGE_MULTIPLIER(287),
        OUTGOING_COMBAT_DAMAGE_CAP(288),
        INCOMING_COMBAT_DAMAGE_MULTIPLIER(289),
        INCOMING_COMBAT_DAMAGE_CAP(290),
        CURRENT_SPELLPOWER(291),
        ARMOR(292),
        MORPH(293),
        IS_MORPHED(294),
        TEMP_RESOURCES(295),
        RECALL_OWED(296),
        NUM_ATTACKS_THIS_TURN(297),
        NEXT_ALLY_BUFF(302),
        MAGNET(303),
        FIRST_CARD_PLAYED_THIS_TURN(304),
        MULLIGAN_STATE(305),
        TAUNT_READY(306),
        STEALTH_READY(307),
        CHARGE_READY(308),
        CANT_BE_TARGETED_BY_ABILITIES(311),
        SHOULD_EXIT_COMBAT(312),
        CREATOR(313),
        CANT_BE_DISPELLED(314),
        DIVINE_SHIELD_READY(314),
        PARENT_CARD(316),
        NUM_MINIONS_PLAYED_THIS_TURN(317),
        PREDAMAGE(318),
        TARGETING_ARROW_TEXT(325),
        ENCHANTMENT_BIRTH_VISUAL(330),
        ENCHANTMENT_IDLE_VISUAL(331),
        CANT_BE_TARGETED_BY_HERO_POWERS(332),
        HEALTH_MINIMUM(337),
        SILENCE(339),
        COUNTER(340),
        ARTIST_NAME(342),
        HAND_REVEALED(348),
        ADJACENT_BUFF(350),
        FLAVOR_TEXT(351),
        FORCED_PLAY(352),
        LOW_HEALTH_THRESHOLD(353),
        IGNORE_DAMAGE_OFF(354),
        SPELL_POWER_DOUBLE(356),
        HEALING_DOUBLE(357),
        NUM_OPTIONS_PLAYED_THIS_TURN(358),
        NUM_OPTIONS(359),
        TO_BE_DESTROYED(360),
        AURA(362),
        POISONOUS(363),
        HOW_TO_EARN(364),
        HOW_TO_EARN_GOLDEN(365),
        AFFECTED_BY_SPELL_POWER(370),
        IMMUNE_WHILE_ATTACKING(373);

        public final int id;

        private GameTag(int id) {
            this.id = id;
        }

        @Override
        public int getId() {
            return id;
        }
    }

    public enum MetaType
            implements IDEnum {
        TARGET(1),
        DAMAGE(2),
        HEALING(3);

        public final int id;

        private MetaType(int id) {
            this.id = id;
        }

        @Override
        public int getId() {
            return id;
        }
    }

    /**
     * How rare a card is (duh?).
     */
    public enum CardRarity {
        INVALID,
        COMMON,
        FREE,
        RARE,
        EPIC,
        LEGENDARY
    }

    /**
     * Steps during gameplay
     */
    public enum Step {
        INVALID,
        BEGIN_FIRST,
        BEGIN_SHUFFLE,
        BEGIN_DRAW,
        BEGIN_MULLIGAN,
        MAIN_BEGIN,
        MAIN_READY,
        MAIN_RESOURCE,
        MAIN_DRAW,
        MAIN_START,
        MAIN_ACTION,
        MAIN_COMBAT,
        MAIN_END,
        MAIN_NEXT,
        FINAL_WRAPUP,
        FINAL_GAMEOVER,
        MAIN_CLEANUP,
        MAIN_START_TRIGGERS
    }

    /**
     * Where a card resides
     */
    public enum Zone {
        INVALID,
        PLAY,
        DECK,
        HAND,
        GRAVEYARD,
        REMOVED_FROM_GAME,
        SET_ASIDE,
        SECRET
    }

    /**
     * The type of a card (hero, minion, weapon, etc, as well as a few technical "cards").
     */
    public enum CardType {
        INVALID,
        GAME,
        PLAYER,
        HERO,
        MINION,
        ABILITY,
        ENCHANTMENT,
        WEAPON,
        ITEM,
        TOKEN,
        HERO_POWER
    }

    public enum PlayState {
        INVALID,
        PLAYING,
        WINNING,
        LOSING,
        WON,
        LOST,
        TIED,
        DISCONNECTED,
        QUIT
    }

    /**
     * The current state of mulligan
     */
    public enum MulliganState {
        INVALID,
        INPUT,
        DEALING,
        WAITING,
        DONE
    }

    /**
     * What sort of action is being performed.
     */
    public enum ActionSubType
            implements IDEnum {
        ATTACK(1),
        CONTINOUS(2),
        POWER(3),
        SCRIPT(4),
        TRIGGER(5),
        DEATHS(6),
        PLAY(7),
        FATIGUE(8);

        public final int id;

        private ActionSubType(int id) {
            this.id = id;
        }

        @Override
        public int getId() {
            return id;
        }
    }

    /**
     * All of the packets.
     */
    public enum PacketType
            implements IDEnum {
        REQUEST_GAME_STATE(1, Packet001RequestGameState.class),
        CHOOSE_OPTION(2, Packet002ChooseOption.class),
        MULLIGAN_PICK(3, Packet003MulliganPick.class),
        PRE_CAST(4, Packet004PreCast.class),
        DEBUG_MESSAGE(5, Packet005DebugMessage.class),
        CLIENT_PACKET(6, Packet006ClientPacket.class),
        START_GAME_STATE(7, Packet007StartGameState.class),
        FINISH_GAME_STATE(8, Packet008FinishGameState.class),
        TURN_TIMER(9, Packet009TurnTimer.class),
        NACK_OPTION(10, Packet010NackOption.class),
        GIVE_UP(11, Packet011PlayerConcede.class),
        GAME_CANCELLED(12, Packet012GameCancelled.class),
        ALL_OPTIONS(14, Packet014AllOptions.class),
        USER_UI(15, Packet015UserUI.class),
        GAME_SETUP(16, Packet016GameSetup.class),
        MULLIGAN_RESULT(17, Packet017MulliganResult.class),
        PRE_LOAD(18, Packet018PreLoad.class),
        GAME_STATE(19, Packet019GameState.class),
        NOTIFICATION(21, Packet021Notification.class),
        AUTO_LOGIN(103, Packet103AutoLogin.class),
        BEGIN_PLAYING(113, Packet113ReadyMode.class),
        DEBUG_CONSOLE_COMMAND(123, Packet123DebugConsoleCommand.class),
        DEBUG_CONSOLE_RESPONSE(124, Packet124DebugConsoleResponse.class),
        GAME_STARTING(114, Packet114GameStarting.class),
        AURORA_HANDSHAKE(168, Packet168AuroraHandshake.class);

        public final int id;
        public final Class<? extends CaptureStruct> clazz;

        private PacketType(int i, Class<? extends CaptureStruct> clazz) {
            id = i;
            this.clazz = clazz;
        }

        @Override
        public int getId() {
            return id;
        }
    }

    public enum TagState {
        INVALID,
        LOADING,
        RUNNING,
        COMPLETE
    }

    public enum Faction {
        INVALID,
        HORDE,
        ALLIANCE,
        NEUTRAL
    }

    /**
     * What race a card is. Used for certain card effects.
     */
    public enum Race {
        INVALID,
        BLOODELF,
        DRAENEI,
        DWARF,
        GNOME,
        GOBLIN,
        HUMAN,
        NIGHTELF,
        ORC,
        TAUREN,
        TROLL,
        UNDEAD,
        WORGEN,
        GOBLIN2,
        MURLOC,
        DEMON,
        SCOURGE,
        MECHANICAL,
        ELEMENTAL,
        OGRE,
        PET,
        TOTEM,
        NERUBIAN,
        PIRATE,
        DRAGON
    }

    public enum OptionType
            implements IDEnum {
        PASS(1),
        END_TURN(2),
        ACTION(3);

        public final int id;

        private OptionType(int id) {
            this.id = id;
        }

        @Override
        public int getId() {
            return id;
        }
    }

    /**
     * Various emotes that a player can use (greet, well played, oops, etc).
     */
    public enum Emote implements IDEnum {

        NONE(0),
        GREETING(1),
        WELL_PLAYED(2),
        OOPS(3),
        THREATEN(4),
        THANKS(5),
        SORRY(6);

        public final int id;

        private Emote(int id) {
            this.id = id;
        }

        @Override
        public int getId() {
            return id;
        }
    }

}
