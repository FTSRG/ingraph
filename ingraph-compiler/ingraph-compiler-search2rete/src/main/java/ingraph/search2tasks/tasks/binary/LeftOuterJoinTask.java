package ingraph.search2tasks.tasks.binary;

import ingraph.search2tasks.tasks.Task;
import org.eclipse.xtend.lib.annotations.Data;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@Data
@SuppressWarnings("all")
public class LeftOuterJoinTask implements Task {
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    return result;
  }
  
  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    LeftOuterJoinTask other = (LeftOuterJoinTask) obj;
    return true;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    return b.toString();
  }
}
