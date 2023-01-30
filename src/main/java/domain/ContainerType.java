package domain;

import domain.constraints.MoveInformation;

/**
 * tmp.Default types in the domain of solitaire.
 *
 * To add new kinds of containers, just have an enum extend this interface
 *
 * Reason for having ContainerType extend MoveInformation is to enable individual elements
 * to also be used within the description of valid moves.
 */
public interface ContainerType extends MoveInformation {
     /** Every entity must be capable of returning a (unique) name. */
     String getName();

     /** By default, each container refers to an element of potentially multiple cards. */
     default boolean isSingleCard() { return false; }
}
