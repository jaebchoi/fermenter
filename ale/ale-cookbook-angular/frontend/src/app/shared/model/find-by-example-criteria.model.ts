import { FindByExampleCriteriaBase } from '../../generated/model-base/find-by-example-criteria-base.model';
import { SortWrapper } from './sort-wrapper.model'

/*******************************************************
 * Find By Example Criteria Model
 * This is used to specify criteria to find entities using the find by example maintenance service.
 * 
 * Generated Code - DO MODIFY
 * Template = find-by-example-criteria.model.ts.vm
 *******************************************************/
export class FindByExampleCriteria<T> extends FindByExampleCriteriaBase<T> {
  constructor(sortWrapper: SortWrapper, probe?: T, containsMatch?: boolean, startPage?: number, count?: number) {
    super(sortWrapper, probe, containsMatch, startPage, count);
  }
}
